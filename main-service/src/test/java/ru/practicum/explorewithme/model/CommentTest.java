package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentTest {

    @Test
    void testNoArgsConstructor() {
        Comment comment = new Comment();
        assertNotNull(comment);
        assertNull(comment.getId());
        assertNull(comment.getText());
        assertNull(comment.getEvent());
        assertNull(comment.getAuthor());
        assertNull(comment.getStatus());
        assertNull(comment.getCreatedOn());
        assertNull(comment.getUpdatedOn());
    }

    @Test
    void testAllArgsConstructor() {
        User author = new User();
        Event event = new Event();
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime updatedOn = LocalDateTime.now().plusHours(1);

        Comment comment = new Comment(1L, "Test text", event, author,
                CommentStatus.PENDING, createdOn, updatedOn);

        assertEquals(1L, comment.getId());
        assertEquals("Test text", comment.getText());
        assertEquals(event, comment.getEvent());
        assertEquals(author, comment.getAuthor());
        assertEquals(CommentStatus.PENDING, comment.getStatus());
        assertEquals(createdOn, comment.getCreatedOn());
        assertEquals(updatedOn, comment.getUpdatedOn());
    }

    @Test
    void testBuilder() {
        User author = new User();
        Event event = new Event();
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime updatedOn = LocalDateTime.now().plusHours(1);

        Comment comment = Comment.builder()
                .id(1L)
                .text("Test text")
                .event(event)
                .author(author)
                .status(CommentStatus.PUBLISHED)
                .createdOn(createdOn)
                .updatedOn(updatedOn)
                .build();

        assertEquals(1L, comment.getId());
        assertEquals("Test text", comment.getText());
        assertEquals(event, comment.getEvent());
        assertEquals(author, comment.getAuthor());
        assertEquals(CommentStatus.PUBLISHED, comment.getStatus());
        assertEquals(createdOn, comment.getCreatedOn());
        assertEquals(updatedOn, comment.getUpdatedOn());
    }

    @Test
    void testSettersAndGetters() {
        Comment comment = new Comment();
        User author = new User();
        Event event = new Event();
        LocalDateTime createdOn = LocalDateTime.now();
        LocalDateTime updatedOn = LocalDateTime.now().plusHours(1);

        comment.setId(1L);
        comment.setText("Test text");
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setStatus(CommentStatus.REJECTED);
        comment.setCreatedOn(createdOn);
        comment.setUpdatedOn(updatedOn);

        assertEquals(1L, comment.getId());
        assertEquals("Test text", comment.getText());
        assertEquals(event, comment.getEvent());
        assertEquals(author, comment.getAuthor());
        assertEquals(CommentStatus.REJECTED, comment.getStatus());
        assertEquals(createdOn, comment.getCreatedOn());
        assertEquals(updatedOn, comment.getUpdatedOn());
    }

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = Comment.builder().id(1L).text("Text 1").build();
        Comment comment2 = Comment.builder().id(1L).text("Text 2").build();
        Comment comment3 = Comment.builder().id(2L).text("Text 1").build();

        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertNotEquals(comment1.hashCode(), comment3.hashCode());
    }

    @Test
    void testToString() {
        Comment comment = Comment.builder()
                .id(1L)
                .text("Test text")
                .status(CommentStatus.PENDING)
                .build();

        String toString = comment.toString();
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("text=Test text"));
        assertTrue(toString.contains("status=PENDING"));
        assertFalse(toString.contains("event"));
        assertFalse(toString.contains("author"));
    }
}