package ru.practicum.stats.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    void handleIllegalArgumentException_shouldReturnCorrectErrorResponse() {
        String errorMessage = "Test error message";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        ErrorResponse response = errorHandler.handleIllegalArgumentException(exception);

        assertNotNull(response);
        assertEquals("BAD_REQUEST", response.getError());
        assertEquals(errorMessage, response.getMessage());
        assertEquals("Incorrectly made request.", response.getReason());
        assertEquals(HttpStatus.BAD_REQUEST.name(), response.getStatus());
        assertDoesNotThrow(() -> LocalDateTime.parse(response.getTimestamp(), FORMATTER));
    }

    @Test
    void handleThrowable_shouldReturnCorrectErrorResponse() {
        String errorMessage = "Internal server error";
        Throwable throwable = new Throwable(errorMessage);

        ErrorResponse response = errorHandler.handleThrowable(throwable);

        assertNotNull(response);
        assertEquals("INTERNAL_SERVER_ERROR", response.getError());
        assertEquals(errorMessage, response.getMessage());
        assertEquals("Internal server error.", response.getReason());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.name(), response.getStatus());
        assertDoesNotThrow(() -> LocalDateTime.parse(response.getTimestamp(), FORMATTER));
    }

    @Test
    void handleIllegalArgumentException_shouldHandleNullMessage() {
        IllegalArgumentException exception = new IllegalArgumentException();

        ErrorResponse response = errorHandler.handleIllegalArgumentException(exception);

        assertNotNull(response);
        assertNull(response.getMessage());
    }

    @Test
    void handleThrowable_shouldHandleNullMessage() {
        Throwable throwable = new Throwable();

        ErrorResponse response = errorHandler.handleThrowable(throwable);

        assertNotNull(response);
        assertNull(response.getMessage());
    }
}
