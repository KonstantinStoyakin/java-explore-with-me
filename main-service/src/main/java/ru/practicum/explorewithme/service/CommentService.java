package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateRequest);

    void deleteComment(Long userId, Long commentId);

    List<CommentDto> getUserComments(Long userId, Integer from, Integer size);

    List<CommentDto> getEventComments(Long eventId, Integer from, Integer size);

    CommentDto getComment(Long commentId);

    void deleteCommentByAdmin(Long commentId);

    CommentDto moderateComment(Long commentId, Boolean approve);

    List<CommentDto> getPendingComments(Integer from, Integer size);
}