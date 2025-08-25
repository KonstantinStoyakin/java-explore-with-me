package ru.practicum.explorewithme.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicEventController.class)
class PublicEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private EventShortDto eventShortDto;

    private EventFullDto eventFullDto;

    @BeforeEach
    void setUp() {
        eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setTitle("Test Event");
        eventShortDto.setAnnotation("Annotation");
        eventShortDto.setEventDate(LocalDateTime.now());
        eventShortDto.setPaid(false);
        eventShortDto.setConfirmedRequests(0);
        eventShortDto.setViews(0L);

        eventFullDto = new EventFullDto();
        eventFullDto.setId(1L);
        eventFullDto.setTitle("Test Event");
        eventFullDto.setAnnotation("Annotation");
        eventFullDto.setEventDate(LocalDateTime.now());
        eventFullDto.setCreatedOn(LocalDateTime.now());
        eventFullDto.setDescription("Description");
        eventFullDto.setState("PUBLISHED");
        eventFullDto.setParticipantLimit(0);
        eventFullDto.setConfirmedRequests(0);
        eventFullDto.setViews(0L);
        eventFullDto.setPaid(false);
        eventFullDto.setRequestModeration(false);
    }

    @Test
    void searchPublishedEvents_ShouldReturnList() throws Exception {
        Mockito.when(eventService.searchPublishedEvents(
                        anyString(), anyList(), anyBoolean(), any(), any(),
                        anyBoolean(), anyString(), anyInt(), anyInt(), any(HttpServletRequest.class)))
                .thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .param("categories", "1,2")
                        .param("paid", "false")
                        .param("rangeStart", "2023-01-01 10:00:00")
                        .param("rangeEnd", "2023-01-02 10:00:00")
                        .param("onlyAvailable", "true")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Event"));
    }

    @Test
    void searchPublishedEvents_WithDefaultParams_ShouldReturnList() throws Exception {
        Mockito.when(eventService.searchPublishedEvents(
                        isNull(), isNull(), isNull(), isNull(), isNull(),
                        isNull(), isNull(), eq(0), eq(10), any(HttpServletRequest.class)))
                .thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getPublishedEvent_ShouldReturnEvent() throws Exception {
        Mockito.when(eventService.getPublishedEvent(eq(1L), any(HttpServletRequest.class)))
                .thenReturn(eventFullDto);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Event"));
    }
}