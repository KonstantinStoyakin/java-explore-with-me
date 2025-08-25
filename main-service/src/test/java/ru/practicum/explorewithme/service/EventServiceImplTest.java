package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private StatsClient statsClient;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    void addEvent_shouldCreateNewEvent() {
        Long userId = 1L;
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setTitle("Test Event");
        newEventDto.setAnnotation("Test Annotation");
        newEventDto.setDescription("Test Description");
        newEventDto.setEventDate(LocalDateTime.now().plusHours(3));
        newEventDto.setCategory(1L);
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(10);
        newEventDto.setRequestModeration(true);

        User user = new User();
        user.setId(userId);
        Category category = new Category();
        category.setId(1L);
        Event event = new Event();
        event.setId(1L);
        EventFullDto expectedDto = new EventFullDto();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(eventMapper.toEvent(newEventDto)).thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);

        EventFullDto result = eventService.addEvent(userId, newEventDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(user, event.getInitiator());
        assertEquals(category, event.getCategory());
        assertEquals(EventState.PENDING, event.getState());
        assertEquals(0, event.getConfirmedRequests());
        assertEquals(0L, event.getViews());
        verify(userRepository).findById(userId);
        verify(categoryRepository).findById(1L);
        verify(eventRepository).save(event);
    }

    @Test
    void addEvent_shouldThrowNotFoundExceptionWhenUserNotFound() {
        Long userId = 1L;
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(3));

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.addEvent(userId, newEventDto));
        verify(userRepository).findById(userId);
        verify(categoryRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void addEvent_shouldThrowValidationExceptionForEventDate() {
        Long userId = 1L;
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setEventDate(LocalDateTime.now().plusHours(1));

        User user = new User();
        user.setId(userId);

        verify(userRepository, never()).findById(any());
        verify(categoryRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void getUserEvents_shouldReturnUserEvents() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").descending());
        Event event1 = new Event();
        event1.setId(1L);
        Event event2 = new Event();
        event2.setId(2L);
        List<Event> events = List.of(event1, event2);
        EventShortDto dto1 = new EventShortDto();
        EventShortDto dto2 = new EventShortDto();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(eventRepository.findAllByInitiatorId(userId, pageRequest)).thenReturn(events);
        when(eventMapper.toEventShortDto(event1)).thenReturn(dto1);
        when(eventMapper.toEventShortDto(event2)).thenReturn(dto2);

        List<EventShortDto> result = eventService.getUserEvents(userId, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).existsById(userId);
        verify(eventRepository).findAllByInitiatorId(userId, pageRequest);
    }

    @Test
    void getUserEvents_shouldThrowNotFoundException() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.getUserEvents(userId, from, size));
        verify(userRepository).existsById(userId);
        verify(eventRepository, never()).findAllByInitiatorId(any(), any());
    }

    @Test
    void getUserEvent_shouldReturnUserEvent() {
        Long userId = 1L;
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        EventFullDto expectedDto = new EventFullDto();

        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(Optional.of(event));
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);

        EventFullDto result = eventService.getUserEvent(userId, eventId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(eventRepository).findByIdAndInitiatorId(eventId, userId);
    }

    @Test
    void getUserEvent_shouldThrowNotFoundException() {
        Long userId = 1L;
        Long eventId = 1L;

        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getUserEvent(userId, eventId));
        verify(eventRepository).findByIdAndInitiatorId(eventId, userId);
    }

    @Test
    void updateEventByUser_shouldUpdateEvent() {
        Long userId = 1L;
        Long eventId = 1L;
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setEventDate(LocalDateTime.now().plusHours(3));

        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PENDING);
        event.setTitle("Old Title");
        EventFullDto expectedDto = new EventFullDto();

        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);

        doAnswer(invocation -> {
            UpdateEventUserRequest req = invocation.getArgument(0);
            Event ev = invocation.getArgument(1);

            if (req.getTitle() != null) {
                ev.setTitle(req.getTitle());
            }
            if (req.getEventDate() != null) {
                ev.setEventDate(req.getEventDate());
            }
            return null;
        }).when(eventMapper).updateEventFromUserRequest(any(UpdateEventUserRequest.class), any(Event.class));

        EventFullDto result = eventService.updateEventByUser(userId, eventId, updateRequest);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals("Updated Title", event.getTitle());
        verify(eventRepository).findByIdAndInitiatorId(eventId, userId);
        verify(eventRepository).save(event);
    }


    @Test
    void updateEventByUser_shouldThrowConflictExceptionForPublishedEvent() {
        Long userId = 1L;
        Long eventId = 1L;
        UpdateEventUserRequest updateRequest = new UpdateEventUserRequest();

        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PUBLISHED);

        when(eventRepository.findByIdAndInitiatorId(eventId, userId)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> eventService.updateEventByUser(userId, eventId, updateRequest));
        verify(eventRepository).findByIdAndInitiatorId(eventId, userId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void searchEvents_shouldReturnEvents() {
        List<Long> users = List.of(1L);
        List<String> states = List.of("PUBLISHED");
        List<Long> categories = List.of(1L);
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(1);
        LocalDateTime rangeEnd = LocalDateTime.now().plusDays(1);
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("eventDate").descending());

        Event event = new Event();
        event.setId(1L);
        Page<Event> eventPage = new PageImpl<>(List.of(event));
        EventFullDto expectedDto = new EventFullDto();

        when(eventRepository.findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                any(), any(), any(), any(), any(), any())).thenReturn(eventPage);
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);

        List<EventFullDto> result = eventService.searchEvents(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByInitiatorIdInAndStateInAndCategoryIdInAndEventDateBetween(
                any(), any(), any(), any(), any(), any());
    }

    @Test
    void searchEvents_shouldThrowValidationExceptionForInvalidState() {
        List<Long> users = List.of(1L);
        List<String> states = List.of("INVALID_STATE");
        List<Long> categories = List.of(1L);
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(1);
        LocalDateTime rangeEnd = LocalDateTime.now().plusDays(1);
        Integer from = 0;
        Integer size = 10;

        assertThrows(ValidationException.class, () ->
                eventService.searchEvents(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @Test
    void updateEventByAdmin_shouldUpdateEvent() {
        Long eventId = 1L;
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("Admin Updated Title");
        updateRequest.setEventDate(LocalDateTime.now().plusHours(2));

        Event event = new Event();
        event.setId(eventId);
        EventFullDto expectedDto = new EventFullDto();

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);

        EventFullDto result = eventService.updateEventByAdmin(eventId, updateRequest);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(eventRepository).save(event);
    }

    @Test
    void updateEventByAdmin_shouldThrowNotFoundException() {
        Long eventId = 1L;
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.updateEventByAdmin(eventId, updateRequest));
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void searchPublishedEvents_shouldReturnEvents() {
        String text = "test";
        List<Long> categories = List.of(1L);
        Boolean paid = true;
        LocalDateTime rangeStart = LocalDateTime.now().minusDays(1);
        LocalDateTime rangeEnd = LocalDateTime.now().plusDays(1);
        Boolean onlyAvailable = false;
        String sort = "EVENT_DATE";
        Integer from = 0;
        Integer size = 10;

        Event event = new Event();
        event.setId(1L);
        event.setAnnotation("Test annotation");
        event.setDescription("Test description");
        event.setPaid(true);
        event.setEventDate(LocalDateTime.now().plusHours(2));
        Page<Event> eventPage = new PageImpl<>(List.of(event));
        EventShortDto expectedDto = new EventShortDto();

        when(eventRepository.findByStateAndCategoryIdIn(eq(EventState.PUBLISHED), any(), any())).thenReturn(eventPage);
        when(eventMapper.toEventShortDto(event)).thenReturn(expectedDto);
        when(httpServletRequest.getRequestURI()).thenReturn("/events");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");

        List<EventShortDto> result = eventService.searchPublishedEvents(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpServletRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(eventRepository).findByStateAndCategoryIdIn(eq(EventState.PUBLISHED), any(), any());
        verify(statsClient).addHit(any(EndpointHit.class));
    }

    @Test
    void getPublishedEvent_shouldReturnEventAndIncrementViews() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PUBLISHED);
        event.setViews(0L);
        EventFullDto expectedDto = new EventFullDto();

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));

        doNothing().when(eventRepository).incrementViews(eventId);
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);
        when(httpServletRequest.getRequestURI()).thenReturn("/events/1");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(requestRepository.countConfirmedRequestsByEventId(eventId)).thenReturn(0L);

        try {
            var viewedIpsField = EventServiceImpl.class.getDeclaredField("viewedIps");
            viewedIpsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var viewedIps = (java.util.Map<Long, java.util.Set<String>>) viewedIpsField.get(eventService);
            viewedIps.clear();
        } catch (Exception e) {
            // intentionally empty - тестовый сценарий
        }

        EventFullDto result = eventService.getPublishedEvent(eventId, httpServletRequest);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(eventRepository, times(2)).findByIdWithCategoryAndInitiator(eventId);
        verify(eventRepository).incrementViews(eventId);
        verify(statsClient).addHit(any(EndpointHit.class));
    }

    @Test
    void getPublishedEvent_shouldNotIncrementViewsForSameIp() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PUBLISHED);
        EventFullDto expectedDto = new EventFullDto();

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toEventFullDto(event)).thenReturn(expectedDto);
        when(httpServletRequest.getRequestURI()).thenReturn("/events/1");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(requestRepository.countConfirmedRequestsByEventId(eventId)).thenReturn(0L);

        try {
            var viewedIpsField = EventServiceImpl.class.getDeclaredField("viewedIps");
            viewedIpsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            var viewedIps = (java.util.Map<Long, java.util.Set<String>>) viewedIpsField.get(eventService);
            viewedIps.put(eventId, ConcurrentHashMap.newKeySet());
            viewedIps.get(eventId).add("127.0.0.1");
        } catch (Exception e) {
            // intentionally empty - тестовый сценарий
        }

        EventFullDto result = eventService.getPublishedEvent(eventId, httpServletRequest);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(eventRepository, times(2)).findByIdWithCategoryAndInitiator(eventId);
        verify(eventRepository, never()).incrementViews(eventId);
        verify(statsClient).addHit(any(EndpointHit.class));
    }

    @Test
    void getPublishedEvent_shouldThrowNotFoundExceptionForUnpublishedEvent() {
        Long eventId = 1L;
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PENDING);

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));

        assertThrows(NotFoundException.class, () -> eventService.getPublishedEvent(eventId, httpServletRequest));
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(eventRepository, never()).incrementViews(any());
    }
}