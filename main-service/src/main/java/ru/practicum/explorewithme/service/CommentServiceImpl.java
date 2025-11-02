package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.dto.UpdateCommentRequest;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.CommentMapper;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.CommentStatus;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.EventState;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto addComment(Long userId, NewCommentDto newCommentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Event event = eventRepository.findById(newCommentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Event with id="
                        + newCommentDto.getEventId() + " was not found"));

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Cannot comment on unpublished event");
        }

        Comment comment = commentMapper.toComment(newCommentDto, user);
        comment.setEvent(event);

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest updateRequest) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, userId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (comment.getStatus() != CommentStatus.PENDING) {
            throw new ConflictException("Only pending comments can be updated");
        }

        if (updateRequest.getText() != null) {
            comment.setText(updateRequest.getText());
        }

        comment.setStatus(CommentStatus.PENDING);
        comment.setUpdatedOn(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        if (!commentRepository.existsByIdAndAuthorId(commentId, userId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }

        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending()
        );
        return commentRepository.findAllByAuthorId(userId, pageRequest).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getEventComments(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").descending()
        );
        return commentRepository.findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageRequest).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (comment.getStatus() != CommentStatus.PUBLISHED) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }

        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentDto moderateComment(Long commentId, Boolean approve) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        if (approve) {
            comment.setStatus(CommentStatus.PUBLISHED);
        } else {
            comment.setStatus(CommentStatus.REJECTED);
        }

        comment.setUpdatedOn(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentDto(updatedComment);
    }

    @Override
    public List<CommentDto> getPendingComments(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(
                from / size,
                size,
                Sort.by("createdOn").ascending()
        );
        return commentRepository.findPendingComments(pageRequest).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}