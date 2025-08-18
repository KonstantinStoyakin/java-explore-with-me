package ru.practicum.stats.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorResponseTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        ErrorResponse response = new ErrorResponse(
                "error",
                "message",
                "reason",
                "status",
                "timestamp"
        );

        assertEquals("error", response.getError());
        assertEquals("message", response.getMessage());
        assertEquals("reason", response.getReason());
        assertEquals("status", response.getStatus());
        assertEquals("timestamp", response.getTimestamp());
    }

    @Test
    void testEqualsAndHashCode() {
        ErrorResponse response1 = new ErrorResponse(
                "error",
                "message",
                "reason",
                "status",
                "timestamp"
        );

        ErrorResponse response2 = new ErrorResponse(
                "error",
                "message",
                "reason",
                "status",
                "timestamp"
        );

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void testNotEquals() {
        ErrorResponse response1 = new ErrorResponse(
                "error1",
                "message",
                "reason",
                "status",
                "timestamp"
        );

        ErrorResponse response2 = new ErrorResponse(
                "error2",
                "message",
                "reason",
                "status",
                "timestamp"
        );

        assertNotEquals(response1, response2);
    }

    @Test
    void testToString() {
        ErrorResponse response = new ErrorResponse(
                "error",
                "message",
                "reason",
                "status",
                "timestamp"
        );

        assertNotNull(response.toString());
        assertTrue(response.toString().contains("ErrorResponse"));
    }
}