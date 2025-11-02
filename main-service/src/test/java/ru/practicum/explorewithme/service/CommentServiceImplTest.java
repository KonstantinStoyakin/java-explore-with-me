package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user@example.com");
        user.setName("Test User");
        return user;
    }

    private Event createEvent(Long id, EventState state) {
        Event event = new Event();
        event.setId(id);
        event.setState(state);
        return event;
    }

    private Comment createComment(Long id, CommentStatus status) {
        return Comment.builder()
                .id(id)
                .text("Test comment")
                .author(createUser(1L))
                .event(createEvent(1L, EventState.PUBLISHED))
                .status(status)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    private CommentDto createCommentDto(Long id) {
        return new CommentDto(id, "Test comment", null, 1L, "PENDING",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void addComment_shouldSuccessfullyAddComment() {
        Long userId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 1L);
        User user = createUser(userId);
        Event event = createEvent(1L, EventState.PUBLISHED);
        Comment comment = createComment(null, CommentStatus.PENDING);
        Comment savedComment = createComment(1L, CommentStatus.PENDING);
        CommentDto commentDto = createCommentDto(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(newCommentDto.getEventId())).thenReturn(Optional.of(event));
        when(commentMapper.toComment(newCommentDto, user)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.toCommentDto(savedComment)).thenReturn(commentDto);

        CommentDto result = commentService.addComment(userId, newCommentDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void addComment_userNotFound_shouldThrowException() {
        Long userId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 1L);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addComment(userId, newCommentDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_eventNotFound_shouldThrowException() {
        Long userId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 1L);
        User user = createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(newCommentDto.getEventId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addComment(userId, newCommentDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_eventNotPublished_shouldThrowException() {
        Long userId = 1L;
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 1L);
        User user = createUser(userId);
        Event event = createEvent(1L, EventState.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventRepository.findById(newCommentDto.getEventId())).thenReturn(Optional.of(event));

        assertThrows(ConflictException.class, () -> commentService.addComment(userId, newCommentDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_shouldSuccessfullyUpdateComment() {
        Long userId = 1L;
        Long commentId = 1L;
        UpdateCommentRequest updateRequest = new UpdateCommentRequest("Updated comment");
        Comment comment = createComment(commentId, CommentStatus.PENDING);
        Comment updatedComment = createComment(commentId, CommentStatus.PENDING);
        updatedComment.setText("Updated comment");
        CommentDto commentDto = createCommentDto(commentId);
        commentDto.setText("Updated comment");

        when(commentRepository.findByIdAndAuthorId(commentId, userId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        when(commentMapper.toCommentDto(updatedComment)).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(userId, commentId, updateRequest);

        assertNotNull(result);
        assertEquals("Updated comment", result.getText());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void updateComment_commentNotFound_shouldThrowException() {
        Long userId = 1L;
        Long commentId = 1L;
        UpdateCommentRequest updateRequest = new UpdateCommentRequest("Updated comment");

        when(commentRepository.findByIdAndAuthorId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> commentService.updateComment(userId, commentId, updateRequest));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void updateComment_notPendingStatus_shouldThrowException() {
        Long userId = 1L;
        Long commentId = 1L;
        UpdateCommentRequest updateRequest = new UpdateCommentRequest("Updated comment");
        Comment comment = createComment(commentId, CommentStatus.PUBLISHED);

        when(commentRepository.findByIdAndAuthorId(commentId, userId)).thenReturn(Optional.of(comment));

        assertThrows(ConflictException.class,
                () -> commentService.updateComment(userId, commentId, updateRequest));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_shouldSuccessfullyDelete() {
        Long userId = 1L;
        Long commentId = 1L;

        when(commentRepository.existsByIdAndAuthorId(commentId, userId)).thenReturn(true);

        commentService.deleteComment(userId, commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void deleteComment_commentNotFound_shouldThrowException() {
        Long userId = 1L;
        Long commentId = 1L;

        when(commentRepository.existsByIdAndAuthorId(commentId, userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> commentService.deleteComment(userId, commentId));
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void getUserComments_shouldReturnComments() {
        Long userId = 1L;
        Comment comment = createComment(1L, CommentStatus.PUBLISHED);
        CommentDto commentDto = createCommentDto(1L);
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.by("createdOn").descending()
        );

        when(userRepository.existsById(userId)).thenReturn(true);
        when(commentRepository.findAllByAuthorId(userId, pageRequest)).thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getUserComments(userId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository, times(1)).findAllByAuthorId(userId, pageRequest);
    }

    @Test
    void getUserComments_userNotFound_shouldThrowException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> commentService.getUserComments(userId, 0, 10));
        verify(commentRepository, never()).findAllByAuthorId(anyLong(), any());
    }

    @Test
    void getEventComments_shouldReturnPublishedComments() {
        Long eventId = 1L;
        Comment comment = createComment(1L, CommentStatus.PUBLISHED);
        CommentDto commentDto = createCommentDto(1L);
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("createdOn").descending());

        when(eventRepository.existsById(eventId)).thenReturn(true);
        when(commentRepository.findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageRequest))
                .thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getEventComments(eventId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository, times(1))
                .findAllByEventIdAndStatus(eventId, CommentStatus.PUBLISHED, pageRequest);
    }

    @Test
    void getComment_shouldReturnPublishedComment() {
        Long commentId = 1L;
        Comment comment = createComment(commentId, CommentStatus.PUBLISHED);
        CommentDto commentDto = createCommentDto(commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = commentService.getComment(commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
    }

    @Test
    void getComment_commentNotFound_shouldThrowException() {
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getComment(commentId));
    }

    @Test
    void getComment_notPublished_shouldThrowException() {
        Long commentId = 1L;
        Comment comment = createComment(commentId, CommentStatus.PENDING);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        assertThrows(NotFoundException.class, () -> commentService.getComment(commentId));
    }

    @Test
    void deleteCommentByAdmin_shouldSuccessfullyDelete() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteCommentByAdmin(commentId);

        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void deleteCommentByAdmin_commentNotFound_shouldThrowException() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> commentService.deleteCommentByAdmin(commentId));
        verify(commentRepository, never()).deleteById(commentId);
    }

    @Test
    void moderateComment_approveTrue_shouldPublishComment() {
        Long commentId = 1L;
        Comment comment = createComment(commentId, CommentStatus.PENDING);
        Comment publishedComment = createComment(commentId, CommentStatus.PUBLISHED);
        CommentDto commentDto = createCommentDto(commentId);
        commentDto.setStatus("PUBLISHED");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(publishedComment);
        when(commentMapper.toCommentDto(publishedComment)).thenReturn(commentDto);

        CommentDto result = commentService.moderateComment(commentId, true);

        assertNotNull(result);
        assertEquals("PUBLISHED", result.getStatus());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void moderateComment_approveFalse_shouldRejectComment() {
        Long commentId = 1L;
        Comment comment = createComment(commentId, CommentStatus.PENDING);
        Comment rejectedComment = createComment(commentId, CommentStatus.REJECTED);
        CommentDto commentDto = createCommentDto(commentId);
        commentDto.setStatus("REJECTED");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(rejectedComment);
        when(commentMapper.toCommentDto(rejectedComment)).thenReturn(commentDto);

        CommentDto result = commentService.moderateComment(commentId, false);

        assertNotNull(result);
        assertEquals("REJECTED", result.getStatus());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void getPendingComments_shouldReturnPendingComments() {
        Comment comment = createComment(1L, CommentStatus.PENDING);
        CommentDto commentDto = createCommentDto(1L);
        PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.by("createdOn").ascending()
        );
        Page<Comment> page = new PageImpl<>(List.of(comment));

        when(commentRepository.findPendingComments(pageRequest)).thenReturn(page);
        when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);

        List<CommentDto> result = commentService.getPendingComments(0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository, times(1)).findPendingComments(pageRequest);
    }
}