package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestServiceImpl requestService;

    @Test
    void addRequest_shouldCreateNewRequest() {
        Long userId = 1L;
        Long eventId = 1L;

        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PUBLISHED);
        event.setInitiator(new User());
        event.getInitiator().setId(2L);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        ParticipationRequestDto expectedDto = new ParticipationRequestDto();

        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.save(any(ParticipationRequest.class))).thenReturn(request);
        when(requestMapper.toParticipationRequestDto(request)).thenReturn(expectedDto);
        when(requestRepository.countConfirmedRequestsByEventId(eventId)).thenReturn(0L);

        ParticipationRequestDto result = requestService.addRequest(userId, eventId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(requestRepository).existsByRequesterIdAndEventId(userId, eventId);
        verify(userRepository).findById(userId);
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(requestRepository).save(any(ParticipationRequest.class));
    }

    @Test
    void addRequest_shouldThrowConflictExceptionForDuplicateRequest() {
        Long userId = 1L;
        Long eventId = 1L;

        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(true);

        assertThrows(ConflictException.class, () -> requestService.addRequest(userId, eventId));
        verify(requestRepository).existsByRequesterIdAndEventId(userId, eventId);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void addRequest_shouldThrowConflictExceptionForInitiator() {
        Long userId = 1L;
        Long eventId = 1L;

        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setId(eventId);
        event.setState(EventState.PUBLISHED);
        event.setInitiator(user);

        when(requestRepository.existsByRequesterIdAndEventId(userId, eventId)).thenReturn(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> requestService.addRequest(userId, eventId));
        verify(requestRepository).existsByRequesterIdAndEventId(userId, eventId);
        verify(userRepository).findById(userId);
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getUserRequests_shouldReturnUserRequests() {
        Long userId = 1L;
        ParticipationRequest request1 = new ParticipationRequest();
        ParticipationRequest request2 = new ParticipationRequest();
        List<ParticipationRequest> requests = List.of(request1, request2);
        ParticipationRequestDto dto1 = new ParticipationRequestDto();
        ParticipationRequestDto dto2 = new ParticipationRequestDto();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(requestRepository.findAllByRequesterId(userId)).thenReturn(requests);
        when(requestMapper.toParticipationRequestDto(request1)).thenReturn(dto1);
        when(requestMapper.toParticipationRequestDto(request2)).thenReturn(dto2);

        List<ParticipationRequestDto> result = requestService.getUserRequests(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).existsById(userId);
        verify(requestRepository).findAllByRequesterId(userId);
    }

    @Test
    void getUserRequests_shouldThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getUserRequests(userId));
        verify(userRepository).existsById(userId);
        verify(requestRepository, never()).findAllByRequesterId(any());
    }

    @Test
    void cancelRequest_shouldCancelRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        ParticipationRequest request = new ParticipationRequest();
        request.setId(requestId);
        request.setStatus(RequestStatus.PENDING);
        request.setEvent(new Event());
        ParticipationRequestDto expectedDto = new ParticipationRequestDto();

        when(requestRepository.findByIdAndRequesterId(requestId, userId)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toParticipationRequestDto(request)).thenReturn(expectedDto);

        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(RequestStatus.CANCELED, request.getStatus());
        verify(requestRepository).findByIdAndRequesterId(requestId, userId);
        verify(requestRepository).save(request);
    }

    @Test
    void cancelRequest_shouldHandleConfirmedRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        Event event = new Event();
        event.setConfirmedRequests(5);
        ParticipationRequest request = new ParticipationRequest();
        request.setId(requestId);
        request.setStatus(RequestStatus.CONFIRMED);
        request.setEvent(event);
        ParticipationRequestDto expectedDto = new ParticipationRequestDto();

        when(requestRepository.findByIdAndRequesterId(requestId, userId)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toParticipationRequestDto(request)).thenReturn(expectedDto);
        when(eventRepository.save(event)).thenReturn(event);

        ParticipationRequestDto result = requestService.cancelRequest(userId, requestId);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(RequestStatus.CANCELED, request.getStatus());
        assertEquals(4, event.getConfirmedRequests());
        verify(requestRepository).findByIdAndRequesterId(requestId, userId);
        verify(requestRepository).save(request);
        verify(eventRepository).save(event);
    }

    @Test
    void getEventParticipants_shouldReturnParticipants() {
        Long userId = 1L;
        Long eventId = 1L;
        ParticipationRequest request1 = new ParticipationRequest();
        ParticipationRequest request2 = new ParticipationRequest();
        List<ParticipationRequest> requests = List.of(request1, request2);
        ParticipationRequestDto dto1 = new ParticipationRequestDto();
        ParticipationRequestDto dto2 = new ParticipationRequestDto();

        when(eventRepository.existsByIdAndInitiatorId(eventId, userId)).thenReturn(true);
        when(requestRepository.findAllByEventId(eventId)).thenReturn(requests);
        when(requestMapper.toParticipationRequestDto(request1)).thenReturn(dto1);
        when(requestMapper.toParticipationRequestDto(request2)).thenReturn(dto2);

        List<ParticipationRequestDto> result = requestService.getEventParticipants(userId, eventId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(eventRepository).existsByIdAndInitiatorId(eventId, userId);
        verify(requestRepository).findAllByEventId(eventId);
    }

    @Test
    void getEventParticipants_shouldThrowNotFoundException() {
        Long userId = 1L;
        Long eventId = 1L;

        when(eventRepository.existsByIdAndInitiatorId(eventId, userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestService.getEventParticipants(userId, eventId));
        verify(eventRepository).existsByIdAndInitiatorId(eventId, userId);
        verify(requestRepository, never()).findAllByEventId(any());
    }

    @Test
    void updateRequestStatus_shouldConfirmRequests() {
        Long userId = 1L;
        Long eventId = 1L;
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L, 2L));
        updateRequest.setStatus("CONFIRMED");

        Event event = new Event();
        event.setId(eventId);
        event.setInitiator(new User());
        event.getInitiator().setId(userId);
        event.setParticipantLimit(3);
        event.setRequestModeration(true);
        event.setConfirmedRequests(0);

        ParticipationRequest request1 = new ParticipationRequest();
        request1.setId(1L);
        request1.setStatus(RequestStatus.PENDING);
        ParticipationRequest request2 = new ParticipationRequest();
        request2.setId(2L);
        request2.setStatus(RequestStatus.PENDING);
        List<ParticipationRequest> requests = List.of(request1, request2);

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(requests);
        when(requestRepository.countConfirmedRequestsByEventId(eventId)).thenReturn(0L);
        when(requestRepository.saveAll(any())).thenReturn(requests);
        when(eventRepository.save(event)).thenReturn(event);

        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(userId, eventId, updateRequest);

        assertNotNull(result);
        assertEquals(2, result.getConfirmedRequests().size());
        assertEquals(0, result.getRejectedRequests().size());
        assertEquals(RequestStatus.CONFIRMED, request1.getStatus());
        assertEquals(RequestStatus.CONFIRMED, request2.getStatus());
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(requestRepository).findAllByIdIn(List.of(1L, 2L));
        verify(requestRepository, times(2)).saveAll(any());
    }

    @Test
    void updateRequestStatus_shouldRejectRequests() {
        Long userId = 1L;
        Long eventId = 1L;
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L, 2L));
        updateRequest.setStatus("REJECTED");

        Event event = new Event();
        event.setId(eventId);
        event.setInitiator(new User());
        event.getInitiator().setId(userId);
        event.setParticipantLimit(3);
        event.setRequestModeration(true);

        ParticipationRequest request1 = new ParticipationRequest();
        request1.setId(1L);
        request1.setStatus(RequestStatus.PENDING);
        ParticipationRequest request2 = new ParticipationRequest();
        request2.setId(2L);
        request2.setStatus(RequestStatus.PENDING);
        List<ParticipationRequest> requests = List.of(request1, request2);

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(requests);
        when(requestRepository.saveAll(any())).thenReturn(requests);

        EventRequestStatusUpdateResult result = requestService.updateRequestStatus(userId, eventId, updateRequest);

        assertNotNull(result);
        assertEquals(0, result.getConfirmedRequests().size());
        assertEquals(2, result.getRejectedRequests().size());
        assertEquals(RequestStatus.REJECTED, request1.getStatus());
        assertEquals(RequestStatus.REJECTED, request2.getStatus());
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(requestRepository).findAllByIdIn(List.of(1L, 2L));
        verify(requestRepository).saveAll(any());
    }

    @Test
    void updateRequestStatus_shouldThrowValidationExceptionForInvalidStatus() {
        Long userId = 1L;
        Long eventId = 1L;
        EventRequestStatusUpdateRequest updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setRequestIds(List.of(1L));
        updateRequest.setStatus("INVALID_STATUS");

        Event event = new Event();
        event.setId(eventId);
        event.setInitiator(new User());
        event.getInitiator().setId(userId);
        event.setParticipantLimit(3);
        event.setRequestModeration(true);

        ParticipationRequest request = new ParticipationRequest();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);

        when(eventRepository.findByIdWithCategoryAndInitiator(eventId)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(request));

        assertThrows(ValidationException.class, () ->
                requestService.updateRequestStatus(userId, eventId, updateRequest));
        verify(eventRepository).findByIdWithCategoryAndInitiator(eventId);
        verify(requestRepository).findAllByIdIn(List.of(1L));
        verify(requestRepository, never()).saveAll(any());
    }
}