package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.EventFullDto;
import ru.practicum.explorewithme.dto.UpdateEventAdminRequest;
import ru.practicum.explorewithme.service.EventService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.isNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminEventController.class)
class AdminEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventService eventService;

    @Test
    void searchEvents_WithAllParameters_ShouldReturnOk() throws Exception {
        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Test Event");

        List<EventFullDto> events = List.of(eventDto);

        Mockito.when(eventService.searchEvents(anyList(), anyList(), anyList(),
                        any(LocalDateTime.class), any(LocalDateTime.class), anyInt(), anyInt()))
                .thenReturn(events);

        mockMvc.perform(get("/admin/events")
                        .param("users", "1,2")
                        .param("states", "PENDING,PUBLISHED")
                        .param("categories", "1,2")
                        .param("rangeStart", "2024-01-01 10:00:00")
                        .param("rangeEnd", "2024-01-02 10:00:00")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Event"));
    }

    @Test
    void searchEvents_WithNoParameters_ShouldReturnOk() throws Exception {
        List<EventFullDto> events = Collections.emptyList();

        Mockito.when(eventService.searchEvents(isNull(), isNull(), isNull(),
                        isNull(), isNull(), anyInt(), anyInt()))
                .thenReturn(events);

        mockMvc.perform(get("/admin/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateEvent_ShouldReturnOk() throws Exception {
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("Updated Event");

        EventFullDto eventDto = new EventFullDto();
        eventDto.setId(1L);
        eventDto.setTitle("Updated Event");

        Mockito.when(eventService.updateEventByAdmin(
                anyLong(),
                any(UpdateEventAdminRequest.class)
        )).thenReturn(eventDto);

        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Event"));
    }

    @Test
    void updateEvent_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("");

        mockMvc.perform(patch("/admin/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchEvents_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/admin/events")
                        .param("rangeStart", "invalid-date")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }
}
