package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.dto.NewEventDto;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.UserShortDto;
import ru.practicum.explorewithme.model.Location;
import ru.practicum.explorewithme.model.RequestStatus;
import ru.practicum.explorewithme.service.EventService;
import ru.practicum.explorewithme.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateEventController.class)
class PrivateEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @MockBean
    private RequestService requestService;

    private EventShortDto eventShortDto;

    private EventFullDto eventFullDto;

    private NewEventDto newEventDto;

    private UpdateEventUserRequest updateEventUserRequest;

    private ParticipationRequestDto participationRequestDto;

    private EventRequestStatusUpdateRequest statusUpdateRequest;

    private EventRequestStatusUpdateResult statusUpdateResult;

    @BeforeEach
    void setUp() {
        eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setTitle("Test Event");
        eventShortDto.setAnnotation("Annotation");
        eventShortDto.setCategory(new CategoryDto(1L, "Category"));
        eventShortDto.setEventDate(LocalDateTime.now());
        eventShortDto.setPaid(false);
        eventShortDto.setConfirmedRequests(0);
        eventShortDto.setViews(0L);
        eventShortDto.setInitiator(new UserShortDto(1L, "User"));

        eventFullDto = new EventFullDto();
        eventFullDto.setId(1L);
        eventFullDto.setTitle("Test Event");
        eventFullDto.setAnnotation("Annotation");
        eventFullDto.setCategory(new CategoryDto(1L, "Category"));
        eventFullDto.setEventDate(LocalDateTime.now());
        eventFullDto.setCreatedOn(LocalDateTime.now());
        eventFullDto.setInitiator(new UserShortDto(1L, "User"));
        eventFullDto.setDescription("Description");
        eventFullDto.setState("PENDING");
        eventFullDto.setParticipantLimit(0);
        eventFullDto.setConfirmedRequests(0);
        eventFullDto.setViews(0L);
        eventFullDto.setLocation(new Location(59.9343f, 30.3351f));
        eventFullDto.setPaid(false);
        eventFullDto.setRequestModeration(false);

        newEventDto = new NewEventDto();
        newEventDto.setTitle("Test Event");
        newEventDto.setAnnotation("This is a detailed abstract that meets the minimum " +
                "length requirement of 20 characters");
        newEventDto.setCategory(1L);
        newEventDto.setDescription("This is a complex description that definitely " +
                "exceeds the minimum required length of 20 characters");
        newEventDto.setEventDate(LocalDateTime.now().plusDays(1));
        newEventDto.setLocation(new Location(59.9343f, 30.3351f));
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(0);
        newEventDto.setRequestModeration(false);

        updateEventUserRequest = new UpdateEventUserRequest();
        updateEventUserRequest.setTitle("Updated Title");

        participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(1L);
        participationRequestDto.setCreated(LocalDateTime.now());
        participationRequestDto.setEvent(1L);
        participationRequestDto.setRequester(1L);
        participationRequestDto.setStatus(RequestStatus.PENDING);

        statusUpdateRequest = new EventRequestStatusUpdateRequest();
        statusUpdateRequest.setRequestIds(List.of(1L));
        statusUpdateRequest.setStatus(RequestStatus.CONFIRMED.toString());

        statusUpdateResult = new EventRequestStatusUpdateResult();
        statusUpdateResult.setConfirmedRequests(List.of(participationRequestDto));
        statusUpdateResult.setRejectedRequests(List.of());
    }

    @Test
    void getUserEvents_ShouldReturnList() throws Exception {
        Mockito.when(eventService.getUserEvents(eq(1L), eq(0), eq(10)))
                .thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Event"));
    }

    @Test
    void addEvent_ShouldCreateEvent() throws Exception {
        Mockito.when(eventService.addEvent(eq(1L), any(NewEventDto.class)))
                .thenReturn(eventFullDto);

        mockMvc.perform(post("/users/1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEventDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    void getUserEvent_ShouldReturnEvent() throws Exception {
        Mockito.when(eventService.getUserEvent(eq(1L), eq(1L)))
                .thenReturn(eventFullDto);

        mockMvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    void updateEvent_ShouldUpdateEvent() throws Exception {
        Mockito.when(eventService.updateEventByUser(eq(1L), eq(1L), any(UpdateEventUserRequest.class)))
                .thenReturn(eventFullDto);

        mockMvc.perform(patch("/users/1/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateEventUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getEventParticipants_ShouldReturnParticipants() throws Exception {
        Mockito.when(requestService.getEventParticipants(eq(1L), eq(1L)))
                .thenReturn(List.of(participationRequestDto));

        mockMvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateRequestStatus_ShouldUpdateStatus() throws Exception {
        Mockito.when(requestService.updateRequestStatus(
                eq(1L),
                eq(1L),
                any(EventRequestStatusUpdateRequest.class)
        )).thenReturn(statusUpdateResult);

        mockMvc.perform(patch("/users/1/events/1/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.confirmedRequests[0].id").value(1));
    }

    @Test
    void addParticipationRequest_ShouldCreateRequest() throws Exception {
        Mockito.when(requestService.addRequest(eq(1L), eq(1L)))
                .thenReturn(participationRequestDto);

        mockMvc.perform(post("/users/1/events/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
}