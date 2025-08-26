package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.model.Comment;
import ru.practicum.explorewithme.model.CommentStatus;
import ru.practicum.explorewithme.model.Event;
import ru.practicum.explorewithme.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommentMapper commentMapper;

    @Test
    void toCommentDto_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);

        Event event = new Event();
        event.setId(1L);

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .event(event)
                .status(CommentStatus.PENDING)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(userMapper.toUserShortDto(any(User.class))).thenReturn(null);

        CommentDto result = commentMapper.toCommentDto(comment);

        assertEquals(1L, result.getId());
        assertEquals("Test comment", result.getText());
        assertEquals(1L, result.getEventId());
        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void toCommentDto_nullInput_shouldReturnNull() {
        CommentDto result = commentMapper.toCommentDto(null);
        assertNull(result);
    }

    @Test
    void toCommentDto_nullEvent_shouldHandleNullEvent() {
        User user = new User();
        user.setId(1L);

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .event(null)
                .status(CommentStatus.PENDING)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(userMapper.toUserShortDto(any(User.class))).thenReturn(null);

        CommentDto result = commentMapper.toCommentDto(comment);

        assertNull(result.getEventId());
    }

    @Test
    void toCommentDto_nullStatus_shouldUseDefault() {
        User user = new User();
        user.setId(1L);

        Event event = new Event();
        event.setId(1L);

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .event(event)
                .status(null)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        when(userMapper.toUserShortDto(any(User.class))).thenReturn(null);

        CommentDto result = commentMapper.toCommentDto(comment);

        assertEquals("PENDING", result.getStatus());
    }

    @Test
    void toComment_shouldMapCorrectly() {
        NewCommentDto newCommentDto = new NewCommentDto("Test comment", 1L);
        User author = new User();
        author.setId(1L);

        Comment result = commentMapper.toComment(newCommentDto, author);

        assertEquals("Test comment", result.getText());
        assertEquals(author, result.getAuthor());
        assertEquals(CommentStatus.PENDING, result.getStatus());
    }

    @Test
    void toComment_nullInput_shouldReturnNull() {
        Comment result = commentMapper.toComment(null, new User());
        assertNull(result);

        result = commentMapper.toComment(new NewCommentDto("test", 1L), null);
        assertNull(result);
    }
}