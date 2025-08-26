package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class CommentDtoTest {

    @Test
    void commentDto_shouldHaveCorrectStructure() {
        LocalDateTime now = LocalDateTime.now();
        UserShortDto author = new UserShortDto(1L, "Test User");

        CommentDto commentDto = new CommentDto(
                1L,
                "Test comment",
                author,
                1L,
                "PENDING",
                now,
                now
        );

        assertEquals(1L, commentDto.getId());
        assertEquals("Test comment", commentDto.getText());
        assertEquals(author, commentDto.getAuthor());
        assertEquals(1L, commentDto.getEventId());
        assertEquals("PENDING", commentDto.getStatus());
        assertEquals(now, commentDto.getCreatedOn());
        assertEquals(now, commentDto.getUpdatedOn());
    }

    @Test
    void commentDto_noArgsConstructor_shouldCreateEmptyObject() {
        CommentDto commentDto = new CommentDto();

        assertNotNull(commentDto);
        assertNull(commentDto.getId());
        assertNull(commentDto.getText());
        assertNull(commentDto.getAuthor());
        assertNull(commentDto.getEventId());
        assertNull(commentDto.getStatus());
        assertNull(commentDto.getCreatedOn());
        assertNull(commentDto.getUpdatedOn());
    }

    @Test
    void commentDto_settersAndGetters_shouldWorkCorrectly() {
        CommentDto commentDto = new CommentDto();
        LocalDateTime now = LocalDateTime.now();
        UserShortDto author = new UserShortDto(1L, "Test User");

        commentDto.setId(1L);
        commentDto.setText("Test comment");
        commentDto.setAuthor(author);
        commentDto.setEventId(1L);
        commentDto.setStatus("PUBLISHED");
        commentDto.setCreatedOn(now);
        commentDto.setUpdatedOn(now);

        assertEquals(1L, commentDto.getId());
        assertEquals("Test comment", commentDto.getText());
        assertEquals(author, commentDto.getAuthor());
        assertEquals(1L, commentDto.getEventId());
        assertEquals("PUBLISHED", commentDto.getStatus());
        assertEquals(now, commentDto.getCreatedOn());
        assertEquals(now, commentDto.getUpdatedOn());
    }

    @Test
    void commentDto_equalsAndHashCode_shouldWorkCorrectly() {
        LocalDateTime now = LocalDateTime.now();
        UserShortDto author = new UserShortDto(1L, "Test User");

        CommentDto commentDto1 = new CommentDto(1L, "Test", author, 1L, "PENDING", now, now);
        CommentDto commentDto2 = new CommentDto(1L, "Test", author, 1L, "PENDING", now, now);

        assertEquals(commentDto1, commentDto2);
        assertEquals(commentDto1.hashCode(), commentDto2.hashCode());
    }

    @Test
    void commentDto_toString_shouldReturnStringRepresentation() {
        LocalDateTime now = LocalDateTime.now();
        UserShortDto author = new UserShortDto(1L, "Test User");

        CommentDto commentDto = new CommentDto(1L, "Test", author, 1L, "PENDING", now, now);

        String toString = commentDto.toString();

        assertNotNull(toString);
        assert(toString.contains("Test"));
        assert(toString.contains("PENDING"));
    }
}