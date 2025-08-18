package ru.practicum.dto;

import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EndpointHitTest {

    @Test
    void testNoArgsConstructor() {
        EndpointHit hit = new EndpointHit();
        assertNotNull(hit);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime timestamp = LocalDateTime.now();
        EndpointHit hit = new EndpointHit(1L, "app", "/uri", "192.168.0.1", timestamp);

        assertEquals(1L, hit.getId());
        assertEquals("app", hit.getApp());
        assertEquals("/uri", hit.getUri());
        assertEquals("192.168.0.1", hit.getIp());
        assertEquals(timestamp, hit.getTimestamp());
    }

    @Test
    void testBuilder() {
        LocalDateTime timestamp = LocalDateTime.now();
        EndpointHit hit = EndpointHit.builder()
                .id(1L)
                .app("app")
                .uri("/uri")
                .ip("192.168.0.1")
                .timestamp(timestamp)
                .build();

        assertEquals(1L, hit.getId());
        assertEquals("app", hit.getApp());
        assertEquals("/uri", hit.getUri());
        assertEquals("192.168.0.1", hit.getIp());
        assertEquals(timestamp, hit.getTimestamp());
    }

    @Test
    void testJsonFormat() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        String json = "{\"app\":\"app\",\"uri\":\"/uri\",\"ip\":\"192.168.0.1\",\"timestamp\":\"2023-01-01 12:00:00\"}";
        EndpointHit hit = mapper.readValue(json, EndpointHit.class);

        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0, 0), hit.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime timestamp = LocalDateTime.now();
        EndpointHit hit1 = new EndpointHit(1L, "app", "/uri", "192.168.0.1", timestamp);
        EndpointHit hit2 = new EndpointHit(1L, "app", "/uri", "192.168.0.1", timestamp);

        assertEquals(hit1, hit2);
        assertEquals(hit1.hashCode(), hit2.hashCode());
    }

    @Test
    void testToString() {
        EndpointHit hit = new EndpointHit();
        assertNotNull(hit.toString());
    }
}
