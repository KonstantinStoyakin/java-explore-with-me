package ru.practicum.explorewithme.exception;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiErrorTest {

    @Test
    void testApiErrorConstructorWithFourParameters() {
        ApiError apiError = new ApiError(
                "400",
                "Bad Request",
                "Validation failed",
                "2023-01-01 10:00:00"
        );

        assertEquals("400", apiError.getStatus());
        assertEquals("Bad Request", apiError.getReason());
        assertEquals("Validation failed", apiError.getMessage());
        assertEquals("2023-01-01 10:00:00", apiError.getTimestamp());
        assertNull(apiError.getErrors());
    }

    @Test
    void testApiErrorConstructorWithFiveParameters() {
        List<String> errors = List.of("Error 1", "Error 2");
        ApiError apiError = new ApiError(
                "400",
                "Bad Request",
                "Validation failed",
                "2023-01-01 10:00:00",
                errors
        );

        assertEquals("400", apiError.getStatus());
        assertEquals("Bad Request", apiError.getReason());
        assertEquals("Validation failed", apiError.getMessage());
        assertEquals("2023-01-01 10:00:00", apiError.getTimestamp());
        assertEquals(2, apiError.getErrors().size());
        assertEquals("Error 1", apiError.getErrors().get(0));
    }

    @Test
    void testSetters() {
        ApiError apiError = new ApiError(
                "400",
                "Bad Request",
                "Validation failed",
                "2023-01-01 10:00:00"
        );
        apiError.setStatus("404");
        apiError.setReason("Not Found");
        apiError.setMessage("Resource not found");
        apiError.setTimestamp("2023-01-01 11:00:00");
        apiError.setErrors(List.of("Not found"));

        assertEquals("404", apiError.getStatus());
        assertEquals("Not Found", apiError.getReason());
        assertEquals("Resource not found", apiError.getMessage());
        assertEquals("2023-01-01 11:00:00", apiError.getTimestamp());
        assertEquals(1, apiError.getErrors().size());
    }
}