package ru.practicum.stats.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class StatsTest {

    @Test
    void testNoArgsConstructor() {
        Stats stats = new Stats();
        assertNotNull(stats);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stats stats = new Stats(
                1L,
                "app",
                "/uri",
                "192.168.0.1",
                timestamp
        );

        assertEquals(1L, stats.getId());
        assertEquals("app", stats.getApp());
        assertEquals("/uri", stats.getUri());
        assertEquals("192.168.0.1", stats.getIp());
        assertEquals(timestamp, stats.getTimestamp());
    }

    @Test
    void testBuilder() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stats stats = Stats.builder()
                .id(1L)
                .app("app")
                .uri("/uri")
                .ip("192.168.0.1")
                .timestamp(timestamp)
                .build();

        assertEquals(1L, stats.getId());
        assertEquals("app", stats.getApp());
        assertEquals("/uri", stats.getUri());
        assertEquals("192.168.0.1", stats.getIp());
        assertEquals(timestamp, stats.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stats stats1 = Stats.builder()
                .id(1L)
                .app("app")
                .uri("/uri")
                .ip("192.168.0.1")
                .timestamp(timestamp)
                .build();

        Stats stats2 = Stats.builder()
                .id(1L)
                .app("app")
                .uri("/uri")
                .ip("192.168.0.1")
                .timestamp(timestamp)
                .build();

        assertEquals(stats1, stats2);
        assertEquals(stats1.hashCode(), stats2.hashCode());
    }

    @Test
    void testToString() {
        Stats stats = new Stats();
        assertNotNull(stats.toString());
    }

    @Test
    void testEntityAnnotations() {
        assertTrue(Stats.class.isAnnotationPresent(jakarta.persistence.Entity.class));

        assertEquals("stats", Stats.class.getAnnotation(jakarta.persistence.Table.class).name());
    }

    @Test
    void testColumnAnnotations() throws NoSuchFieldException {
        assertTrue(Stats.class.getDeclaredField("app").isAnnotationPresent(jakarta.persistence.Column.class));
        assertTrue(Stats.class.getDeclaredField("uri").isAnnotationPresent(jakarta.persistence.Column.class));
        assertTrue(Stats.class.getDeclaredField("ip").isAnnotationPresent(jakarta.persistence.Column.class));
        assertTrue(Stats.class.getDeclaredField("timestamp").isAnnotationPresent(jakarta.persistence.Column.class));

        assertFalse(Stats.class.getDeclaredField("app").getAnnotation(jakarta.persistence.Column.class).nullable());
        assertFalse(Stats.class.getDeclaredField("uri").getAnnotation(jakarta.persistence.Column.class).nullable());
        assertFalse(Stats.class.getDeclaredField("ip").getAnnotation(jakarta.persistence.Column.class).nullable());
        assertFalse(Stats.class.getDeclaredField("timestamp").getAnnotation(jakarta.persistence.Column.class).nullable());
    }
}
