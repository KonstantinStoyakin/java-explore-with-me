package ru.practicum.explorewithme.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleValidationException_ShouldReturnBadRequest() {
        ValidationException exception = new ValidationException("Validation failed");

        ApiError response = errorHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getStatus());
        assertEquals("Incorrectly made request.", response.getReason());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturnBadRequest() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        org.springframework.validation.FieldError fieldError = mock(org.springframework.validation.FieldError.class);
        when(fieldError.getDefaultMessage()).thenReturn("Field error message");
        when(exception.getFieldError()).thenReturn(fieldError);

        ApiError response = errorHandler.handleMethodArgumentNotValidException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getStatus());
        assertNotNull(response);
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        NotFoundException exception = new NotFoundException("Not found");

        ApiError response = errorHandler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getStatus());
        assertEquals("The required object was not found.", response.getReason());
    }

    @Test
    void handleConflictException_ShouldReturnConflict() {
        ConflictException exception = new ConflictException("Conflict");

        ApiError response = errorHandler.handleConflictException(exception);

        assertEquals(HttpStatus.CONFLICT.toString(), response.getStatus());
    }

    @Test
    void handleDataIntegrityViolation_ShouldReturnConflict() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Data integrity violation");

        ApiError response = errorHandler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.CONFLICT.toString(), response.getStatus());
    }

    @Test
    void handleThrowable_ShouldReturnInternalServerError() {
        Throwable throwable = new Throwable("Unexpected error");

        ApiError response = errorHandler.handleThrowable(throwable);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getStatus());
    }

    @Test
    void handleMissingServletRequestParameterException_ShouldReturnBadRequest() {
        MissingServletRequestParameterException exception =
                new MissingServletRequestParameterException("param", "String");

        ApiError response = errorHandler.handleMissingServletRequestParameterException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getStatus());
    }

    @Test
    void handleJsonParseException_ShouldReturnBadRequest() {
        JsonParseException exception = new JsonParseException(null, "Invalid JSON");

        ApiError response = errorHandler.handleJsonParseException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getStatus());
    }

    @Test
    void handleJsonMappingException_ShouldReturnBadRequest() {
        JsonMappingException exception = new JsonMappingException(null, "Mapping error");

        ApiError response = errorHandler.handleJsonMappingException(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getStatus());
    }

    @Test
    void handleRuntimeException_ShouldReturnInternalServerError() {
        RuntimeException exception = new RuntimeException("Runtime error");

        ApiError response = errorHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getStatus());
    }

    @Test
    void handleMethodArgumentTypeMismatch_ShouldReturnBadRequest() {
        MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
        when(exception.getName()).thenReturn("param");
        when(exception.getValue()).thenReturn("invalid");
        when(exception.getRequiredType()).thenReturn((Class) Long.class);

        ApiError response = errorHandler.handleMethodArgumentTypeMismatch(exception);

        assertEquals(HttpStatus.BAD_REQUEST.toString(), response.getStatus());
    }
}