package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CompilationDto;
import ru.practicum.explorewithme.dto.NewCompilationDto;
import ru.practicum.explorewithme.dto.UpdateCompilationRequest;
import ru.practicum.explorewithme.service.CompilationService;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCompilationController.class)
class AdminCompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CompilationService compilationService;

    @Test
    void addCompilation_ShouldReturnCreated() throws Exception {
        NewCompilationDto newCompilationDto = new NewCompilationDto(Collections.emptyList(), false, "Test Compilation");
        CompilationDto compilationDto = new CompilationDto(1L, Collections.emptyList(), false, "Test Compilation");

        Mockito.when(compilationService.addCompilation(any(NewCompilationDto.class))).thenReturn(compilationDto);

        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Compilation"));
    }

    @Test
    void addCompilation_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        NewCompilationDto newCompilationDto = new NewCompilationDto(Collections.emptyList(), false, "");

        mockMvc.perform(post("/admin/compilations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCompilationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCompilation_ShouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(compilationService).deleteCompilation(anyLong());

        mockMvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateCompilation_ShouldReturnOk() throws Exception {
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest(
                Collections.emptyList(),
                true,
                "Updated Compilation"
        );
        CompilationDto compilationDto = new CompilationDto(1L, Collections.emptyList(), true, "Updated Compilation");

        Mockito.when(compilationService.updateCompilation(
                anyLong(),
                any(UpdateCompilationRequest.class)
        )).thenReturn(compilationDto);

        mockMvc.perform(patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Updated Compilation"))
                .andExpect(jsonPath("$.pinned").value(true));
    }

    @Test
    void updateCompilation_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        UpdateCompilationRequest updateRequest = new UpdateCompilationRequest(Collections.emptyList(), true, "");

        mockMvc.perform(patch("/admin/compilations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }
}