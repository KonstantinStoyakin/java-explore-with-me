package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mapper.RequestMapper;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.RequestStatus;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.RequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        validateRequest(userId, eventId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findByIdWithCategoryAndInitiator(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        ParticipationRequest request = createRequest(user, event);
        ParticipationRequest savedRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        validateUserExists(userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            updateConfirmedRequests(request.getEvent(), -1);
        }

        request.setStatus(RequestStatus.CANCELED);
        ParticipationRequest savedRequest = requestRepository.save(request);

        return requestMapper.toParticipationRequestDto(savedRequest);
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        validateEventOwnership(userId, eventId);

        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        Event event = validateEventForStatusUpdate(userId, eventId);
        List<ParticipationRequest> requests = validateRequests(updateRequest);

        return processStatusUpdate(event, updateRequest.getStatus(), requests);
    }

    private void validateRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Request already exists");
        }
    }

    private ParticipationRequest createRequest(User user, Event event) {
        validateEventForParticipation(event, user.getId());

        ParticipationRequest request = new ParticipationRequest();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);
        request.setStatus(determineRequestStatus(event));

        if (request.getStatus() == RequestStatus.CONFIRMED) {
            updateConfirmedRequests(event, 1);
        }

        return request;
    }

    private void validateEventForParticipation(Event event, Long userId) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator cannot add request to own event");
        }

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot participate in unpublished event");
        }

        validateParticipantLimit(event);
    }

    private void validateParticipantLimit(Event event) {
        if (event.getParticipantLimit() < 0) {
            throw new ValidationException("Participant limit cannot be negative");
        }

        if (event.getParticipantLimit() > 0) {
            Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(event.getId());
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit reached");
            }
        }
    }

    private RequestStatus determineRequestStatus(Event event) {
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            return RequestStatus.CONFIRMED;
        }
        return RequestStatus.PENDING;
    }

    private void updateConfirmedRequests(Event event, int delta) {
        event.setConfirmedRequests(event.getConfirmedRequests() + delta);
        eventRepository.save(event);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }

    private void validateEventOwnership(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private Event validateEventForStatusUpdate(Long userId, Long eventId) {
        Event event = eventRepository.findByIdWithCategoryAndInitiator(eventId)
                .filter(e -> e.getInitiator().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new ConflictException("Event does not require request moderation");
        }

        return event;
    }

    private List<ParticipationRequest> validateRequests(EventRequestStatusUpdateRequest updateRequest) {
        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(updateRequest.getRequestIds());

        if (requests.isEmpty()) {
            throw new NotFoundException("No requests found with provided IDs");
        }

        if (requests.stream().anyMatch(r -> !r.getStatus().equals(RequestStatus.PENDING))) {
            throw new ConflictException("Request must have status PENDING");
        }

        return requests;
    }

    private EventRequestStatusUpdateResult processStatusUpdate(Event event, String status,
                                                               List<ParticipationRequest> requests) {
        switch (status) {
            case "CONFIRMED":
                return confirmRequests(event, requests);
            case "REJECTED":
                return rejectRequests(requests);
            default:
                throw new ValidationException("Invalid status: " + status);
        }
    }

    private EventRequestStatusUpdateResult confirmRequests(Event event, List<ParticipationRequest> requests) {
        Long confirmedCount = requestRepository.countConfirmedRequestsByEventId(event.getId());
        int availableSlots = event.getParticipantLimit() - confirmedCount.intValue();

        if (availableSlots <= 0) {
            throw new ConflictException("Participant limit reached");
        }

        List<ParticipationRequest> toConfirm = requests.stream()
                .limit(availableSlots)
                .collect(Collectors.toList());

        List<ParticipationRequest> toReject = requests.stream()
                .skip(availableSlots)
                .collect(Collectors.toList());

        toConfirm.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
        toReject.forEach(r -> r.setStatus(RequestStatus.REJECTED));

        requestRepository.saveAll(toConfirm);
        requestRepository.saveAll(toReject);

        updateConfirmedRequests(event, toConfirm.size());

        return createStatusUpdateResult(toConfirm, toReject);
    }

    private EventRequestStatusUpdateResult rejectRequests(List<ParticipationRequest> requests) {
        requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
        requestRepository.saveAll(requests);

        return createStatusUpdateResult(List.of(), requests);
    }

    private EventRequestStatusUpdateResult createStatusUpdateResult(List<ParticipationRequest> confirmed,
                                                                    List<ParticipationRequest> rejected) {
        List<ParticipationRequestDto> confirmedDtos = confirmed.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        List<ParticipationRequestDto> rejectedDtos = rejected.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());

        return new EventRequestStatusUpdateResult(confirmedDtos, rejectedDtos);
    }
}