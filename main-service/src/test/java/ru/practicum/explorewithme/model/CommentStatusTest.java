package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommentStatusTest {

    @Test
    void commentStatus_shouldHaveCorrectValues() {
        CommentStatus[] values = CommentStatus.values();

        assertEquals(3, values.length);
        assertEquals(CommentStatus.PENDING, CommentStatus.valueOf("PENDING"));
        assertEquals(CommentStatus.PUBLISHED, CommentStatus.valueOf("PUBLISHED"));
        assertEquals(CommentStatus.REJECTED, CommentStatus.valueOf("REJECTED"));
    }

    @Test
    void commentStatus_shouldReturnCorrectNames() {
        assertEquals("PENDING", CommentStatus.PENDING.name());
        assertEquals("PUBLISHED", CommentStatus.PUBLISHED.name());
        assertEquals("REJECTED", CommentStatus.REJECTED.name());
    }

    @Test
    void commentStatus_shouldHaveCorrectOrdinals() {
        assertEquals(0, CommentStatus.PENDING.ordinal());
        assertEquals(1, CommentStatus.PUBLISHED.ordinal());
        assertEquals(2, CommentStatus.REJECTED.ordinal());
    }

    @Test
    void commentStatus_valueOf_shouldWorkCorrectly() {
        assertNotNull(CommentStatus.valueOf("PENDING"));
        assertNotNull(CommentStatus.valueOf("PUBLISHED"));
        assertNotNull(CommentStatus.valueOf("REJECTED"));
    }

    @Test
    void commentStatus_toString_shouldReturnName() {
        assertEquals("PENDING", CommentStatus.PENDING.toString());
        assertEquals("PUBLISHED", CommentStatus.PUBLISHED.toString());
        assertEquals("REJECTED", CommentStatus.REJECTED.toString());
    }
}