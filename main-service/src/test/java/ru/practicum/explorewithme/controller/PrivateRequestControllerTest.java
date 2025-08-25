package ru.practicum.explorewithme.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.model.RequestStatus;
import ru.practicum.explorewithme.service.RequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateRequestController.class)
class PrivateRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    private ParticipationRequestDto participationRequestDto;

    @BeforeEach
    void setUp() {
        participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(1L);
        participationRequestDto.setCreated(LocalDateTime.now());
        participationRequestDto.setEvent(1L);
        participationRequestDto.setRequester(1L);
        participationRequestDto.setStatus(RequestStatus.PENDING);
    }

    @Test
    void getUserRequests_ShouldReturnList() throws Exception {
        Mockito.when(requestService.getUserRequests(eq(1L)))
                .thenReturn(List.of(participationRequestDto));

        mockMvc.perform(get("/users/1/requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void addRequest_ShouldCreateRequest() throws Exception {
        Mockito.when(requestService.addRequest(eq(1L), eq(1L)))
                .thenReturn(participationRequestDto);

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void cancelRequest_ShouldCancelRequest() throws Exception {
        Mockito.when(requestService.cancelRequest(eq(1L), eq(1L)))
                .thenReturn(participationRequestDto);

        mockMvc.perform(patch("/users/1/requests/1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}