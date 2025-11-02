package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicCommentController.class)
class PublicCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentDto createCommentDto(Long id) {
        return new CommentDto(id, "Test comment", null, 1L, "PUBLISHED",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void getEventComments_shouldReturnList() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getEventComments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/events/1/comments")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));

        verify(commentService, times(1)).getEventComments(1L, 0, 10);
    }

    @Test
    void getEventComments_withDefaultParams_shouldUseDefaults() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getEventComments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/events/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(commentService, times(1)).getEventComments(1L, 0, 10);
    }

    @Test
    void getComment_shouldReturnComment() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getComment(anyLong())).thenReturn(commentDto);

        mockMvc.perform(get("/events/1/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        verify(commentService, times(1)).getComment(1L);
    }
}