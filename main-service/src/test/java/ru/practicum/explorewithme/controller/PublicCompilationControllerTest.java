package ru.practicum.explorewithme.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.EventShortDto;
import ru.practicum.explorewithme.service.CompilationService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicCompilationController.class)
class PublicCompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService compilationService;

    private CompilationDto compilationDto;

    @BeforeEach
    void setUp() {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setTitle("Test Event");
        eventShortDto.setAnnotation("Annotation");
        eventShortDto.setEventDate(LocalDateTime.now());
        eventShortDto.setPaid(false);
        eventShortDto.setConfirmedRequests(0);
        eventShortDto.setViews(0L);

        compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setTitle("Test Compilation");
        compilationDto.setPinned(true);
        compilationDto.setEvents(List.of(eventShortDto));
    }

    @Test
    void getCompilations_ShouldReturnList() throws Exception {
        Mockito.when(compilationService.getCompilations(eq(true), eq(0), eq(10)))
                .thenReturn(List.of(compilationDto));

        mockMvc.perform(get("/compilations")
                        .param("pinned", "true")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Compilation"));
    }

    @Test
    void getCompilations_WithoutPinned_ShouldReturnList() throws Exception {
        Mockito.when(compilationService.getCompilations(eq(null), eq(0), eq(10)))
                .thenReturn(List.of(compilationDto));

        mockMvc.perform(get("/compilations")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getCompilation_ShouldReturnCompilation() throws Exception {
        Mockito.when(compilationService.getCompilation(eq(1L)))
                .thenReturn(compilationDto);

        mockMvc.perform(get("/compilations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Compilation"));
    }
}