package ru.practicum.explorewithme.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHit;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewEventDto;
import ru.practicum.explorewithme.dto.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mapper.EventMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final EventMapper eventMapper;

    private final StatsClient statsClient;

    private static final String APP_NAME = "explore-with-me";

    private final Map<Long, Set<String>> viewedIps = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate(), 2);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Category with id=" + newEventDto.getCategory() + " was not found"));

        Event event = eventMapper.toEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0L);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("id").descending());
        return eventRepository.findAllByInitiatorId(userId, pageRequest).stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getState() == EventState.PUBLISHED) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            validateEventDate(updateEventUserRequest.getEventDate(), 2);
        }

        eventMapper.updateEventFromUserRequest(updateEventUserRequest, event);

        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + updateEventUserRequest.getCategory() + " was not found"));
            event.setCategory(category);
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventFullDto> searchEvents(List<Long> users, List<String> states, List<Long> categories,
                                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        try {
            List<EventState> eventStates = parseEventStates(states);

            PageRequest pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate").descending());

            Page<Event> eventPage = eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                    users, eventStates, categories, rangeStart, rangeEnd, pageRequest);

            return eventPage.getContent().stream()
                    .map(eventMapper::toEventFullDto)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid state value");
        }
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findByIdWithCategoryAndInitiator(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (updateEventAdminRequest.getEventDate() != null) {
            validateEventDate(updateEventAdminRequest.getEventDate(), 1);
        }

        eventMapper.updateEventFromAdminRequest(updateEventAdminRequest, event);

        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + updateEventAdminRequest.getCategory() + " was not found"));
            event.setCategory(category);
        }

        Event updatedEvent = eventRepository.save(event);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    @Override
    public List<EventShortDto> searchPublishedEvents(String text, List<Long> categories, Boolean paid,
                                                     LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable, String sort, Integer from, Integer size,
                                                     HttpServletRequest request) {

        validateDateRange(rangeStart, rangeEnd);

        if (from == null) from = 0;
        if (size == null) size = 10;

        PageRequest pageRequest = PageRequest.of(from / size, size, getSort(sort));

        Page<Event> eventPage;
        if (categories != null && !categories.isEmpty()) {
            eventPage = eventRepository.findByStateAndCategoryIdIn(EventState.PUBLISHED, categories, pageRequest);
        } else {
            eventPage = eventRepository.findByState(EventState.PUBLISHED, pageRequest);
        }

        List<Event> events = eventPage.getContent();

        if (text != null && !text.isEmpty()) {
            events = filterByText(events, text);
        }
        if (paid != null) {
            events = filterByPaid(events, paid);
        }
        if (rangeStart != null) {
            events = filterByStartDate(events, rangeStart);
        }
        if (rangeEnd != null) {
            events = filterByEndDate(events, rangeEnd);
        }
        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = filterByAvailability(events);
        }

        sendStats(request);

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EventFullDto getPublishedEvent(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdWithCategoryAndInitiator(id)
                .filter(e -> e.getState() == EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        String ip = request.getRemoteAddr();

        viewedIps.putIfAbsent(id, ConcurrentHashMap.newKeySet());
        Set<String> ips = viewedIps.get(id);

        if (ips.add(ip)) {
            eventRepository.incrementViews(id);
        }

        sendStats(request);

        Event updatedEvent = eventRepository.findByIdWithCategoryAndInitiator(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));

        updateConfirmedRequests(updatedEvent);
        return eventMapper.toEventFullDto(updatedEvent);
    }




    private void validateEventDate(LocalDateTime eventDate, int hours) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
            throw new ValidationException("Event date must be at least " + hours + " hours after current time");
        }
    }

    private List<EventState> parseEventStates(List<String> states) {
        if (states == null || states.isEmpty()) {
            return null;
        }

        return states.stream()
                .map(state -> {
                    try {
                        return EventState.valueOf(state.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new ValidationException("Invalid state value: " + state);
                    }
                })
                .collect(Collectors.toList());
    }

    private Page<Event> getFilteredEvents(List<Long> categories, Pageable pageable) {
        if (categories != null && !categories.isEmpty()) {
            return eventRepository.findByStateAndCategoryIdIn(EventState.PUBLISHED, categories, pageable);
        } else {
            return eventRepository.findByState(EventState.PUBLISHED, pageable);
        }
    }

    private List<Event> applyAdditionalFilters(List<Event> events, String text, Boolean paid,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                               Boolean onlyAvailable) {
        List<Event> filteredEvents = new ArrayList<>(events);

        if (text != null && !text.isEmpty()) {
            filteredEvents = filterByText(filteredEvents, text);
        }

        if (paid != null) {
            filteredEvents = filterByPaid(filteredEvents, paid);
        }

        if (rangeStart != null) {
            filteredEvents = filterByStartDate(filteredEvents, rangeStart);
        }

        if (rangeEnd != null) {
            filteredEvents = filterByEndDate(filteredEvents, rangeEnd);
        }

        if (Boolean.TRUE.equals(onlyAvailable)) {
            filteredEvents = filterByAvailability(filteredEvents);
        }

        return filteredEvents;
    }

    private List<Event> filterByText(List<Event> events, String text) {
        String searchText = text.toLowerCase();
        return events.stream()
                .filter(event -> (event.getAnnotation() != null && event.getAnnotation().toLowerCase().contains(searchText)) ||
                        (event.getDescription() != null && event.getDescription().toLowerCase().contains(searchText)))
                .collect(Collectors.toList());
    }

    private List<Event> filterByPaid(List<Event> events, Boolean paid) {
        return events.stream()
                .filter(event -> event.getPaid().equals(paid))
                .collect(Collectors.toList());
    }

    private List<Event> filterByStartDate(List<Event> events, LocalDateTime rangeStart) {
        return events.stream()
                .filter(event -> event.getEventDate() != null && !event.getEventDate().isBefore(rangeStart))
                .collect(Collectors.toList());
    }

    private List<Event> filterByEndDate(List<Event> events, LocalDateTime rangeEnd) {
        return events.stream()
                .filter(event -> event.getEventDate() != null && !event.getEventDate().isAfter(rangeEnd))
                .collect(Collectors.toList());
    }

    private List<Event> filterByAvailability(List<Event> events) {
        return events.stream()
                .filter(event -> {
                    Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(event.getId());
                    int confirmed = confirmedCount != null ? confirmedCount.intValue() : 0;
                    return event.getParticipantLimit() == 0 || confirmed < event.getParticipantLimit();
                })
                .collect(Collectors.toList());
    }

    private void updateConfirmedRequests(Event event) {
        Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(event.getId());
        event.setConfirmedRequests(confirmedCount != null ? confirmedCount.intValue() : 0);
    }

    private void validateDateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Start date must be before end date");
        }
    }

    private Sort getSort(String sort) {
        if (sort == null) {
            return Sort.unsorted();
        }
        switch (sort.toUpperCase()) {
            case "EVENT_DATE":
                return Sort.by("eventDate").descending();
            case "VIEWS":
                return Sort.by("views").descending();
            default:
                return Sort.unsorted();
        }
    }

    private void sendStats(HttpServletRequest request) {
        try {
            EndpointHit endpointHit = EndpointHit.builder()
                    .app(APP_NAME)
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build();

            statsClient.addHit(endpointHit);
        } catch (Exception e) {
            System.err.println("Failed to send stats: " + e.getMessage());
        }
    }
}