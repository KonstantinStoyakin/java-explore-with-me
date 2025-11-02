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

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCommentController.class)
class AdminCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    private CommentDto createCommentDto(Long id) {
        return new CommentDto(id, "Test comment", null, 1L, "PENDING",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        doNothing().when(commentService).deleteCommentByAdmin(anyLong());

        mockMvc.perform(delete("/admin/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteCommentByAdmin(1L);
    }

    @Test
    void moderateComment_approveTrue_shouldReturnCommentDto() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.moderateComment(anyLong(), anyBoolean())).thenReturn(commentDto);

        mockMvc.perform(patch("/admin/comments/1")
                        .param("approve", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"));

        verify(commentService, times(1)).moderateComment(1L, true);
    }

    @Test
    void moderateComment_approveFalse_shouldReturnCommentDto() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.moderateComment(anyLong(), anyBoolean())).thenReturn(commentDto);

        mockMvc.perform(patch("/admin/comments/1")
                        .param("approve", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(commentService, times(1)).moderateComment(1L, false);
    }

    @Test
    void getPendingComments_shouldReturnList() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getPendingComments(anyInt(), anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/admin/comments/pending")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(commentService, times(1)).getPendingComments(0, 10);
    }

    @Test
    void getPendingComments_withDefaultParams_shouldUseDefaults() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getPendingComments(anyInt(), anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/admin/comments/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(commentService, times(1)).getPendingComments(0, 10);
    }
}