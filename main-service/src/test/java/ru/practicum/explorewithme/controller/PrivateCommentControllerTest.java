package ru.practicum.explorewithme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.dto.UpdateCommentRequest;
import ru.practicum.explorewithme.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateCommentController.class)
class PrivateCommentControllerTest {

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
    void addComment_shouldReturnCreated() throws Exception {
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 1L);
        CommentDto commentDto = createCommentDto(1L);

        when(commentService.addComment(anyLong(), any(NewCommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/users/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"));

        verify(commentService, times(1)).addComment(1L, newCommentDto);
    }

    @Test
    void addComment_withInvalidData_shouldReturnBadRequest() throws Exception {
        NewCommentDto invalidDto = new NewCommentDto("", null);

        mockMvc.perform(post("/users/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateComment_shouldReturnUpdatedComment() throws Exception {
        UpdateCommentRequest updateRequest = new UpdateCommentRequest("Updated comment");
        CommentDto commentDto = createCommentDto(1L);
        commentDto.setText("Updated comment");

        when(commentService.updateComment(anyLong(), anyLong(), any(UpdateCommentRequest.class)))
                .thenReturn(commentDto);

        mockMvc.perform(patch("/users/1/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated comment"));

        verify(commentService, times(1)).updateComment(1L, 1L, updateRequest);
    }

    @Test
    void deleteComment_shouldReturnNoContent() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());

        mockMvc.perform(delete("/users/1/comments/1"))
                .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(1L, 1L);
    }

    @Test
    void getUserComments_shouldReturnList() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getUserComments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/users/1/comments")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(commentService, times(1)).getUserComments(1L, 0, 10);
    }

    @Test
    void getUserComments_withDefaultParams_shouldUseDefaults() throws Exception {
        CommentDto commentDto = createCommentDto(1L);
        when(commentService.getUserComments(anyLong(), anyInt(), anyInt())).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/users/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));

        verify(commentService, times(1)).getUserComments(1L, 0, 10);
    }
}