package ru.practicum.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ViewStatsTest {

    @Test
    void testNoArgsConstructor() {
        ViewStats stats = new ViewStats();
        assertNotNull(stats);
    }

    @Test
    void testAllArgsConstructor() {
        ViewStats stats = new ViewStats("app", "/uri", 10L);

        assertEquals("app", stats.getApp());
        assertEquals("/uri", stats.getUri());
        assertEquals(10L, stats.getHits());
    }

    @Test
    void testBuilder() {
        ViewStats stats = ViewStats.builder()
                .app("app")
                .uri("/uri")
                .hits(10L)
                .build();

        assertEquals("app", stats.getApp());
        assertEquals("/uri", stats.getUri());
        assertEquals(10L, stats.getHits());
    }

    @Test
    void testEqualsAndHashCode() {
        ViewStats stats1 = new ViewStats("app", "/uri", 10L);
        ViewStats stats2 = new ViewStats("app", "/uri", 10L);

        assertEquals(stats1, stats2);
        assertEquals(stats1.hashCode(), stats2.hashCode());
    }

    @Test
    void testToString() {
        ViewStats stats = new ViewStats();
        assertNotNull(stats.toString());
    }
}
