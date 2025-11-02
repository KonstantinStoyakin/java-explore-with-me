package ru.practicum.explorewithme.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCommentRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void updateCommentRequest_shouldHaveCorrectStructure() {
        UpdateCommentRequest request = new UpdateCommentRequest("Updated comment");

        assertEquals("Updated comment", request.getText());
    }

    @Test
    void updateCommentRequest_noArgsConstructor_shouldCreateEmptyObject() {
        UpdateCommentRequest request = new UpdateCommentRequest();

        assertNotNull(request);
        assertNull(request.getText());
    }

    @Test
    void updateCommentRequest_settersAndGetters_shouldWorkCorrectly() {
        UpdateCommentRequest request = new UpdateCommentRequest();

        request.setText("Updated comment");

        assertEquals("Updated comment", request.getText());
    }

    @Test
    void updateCommentRequest_validation_shouldPassForValidData() {
        UpdateCommentRequest validRequest = new UpdateCommentRequest("Valid update");

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void updateCommentRequest_validation_shouldPassForNullText() {
        UpdateCommentRequest request = new UpdateCommentRequest(null);

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void updateCommentRequest_validation_shouldDetectEmptyText() {
        UpdateCommentRequest invalidRequest = new UpdateCommentRequest("");

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("text")));
    }

    @Test
    void updateCommentRequest_validation_shouldDetectTextTooLong() {
        String longText = "a".repeat(2001);
        UpdateCommentRequest invalidRequest = new UpdateCommentRequest(longText);

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(invalidRequest);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("text")));
    }

    @Test
    void updateCommentRequest_validation_shouldPassForMinLengthText() {
        UpdateCommentRequest validRequest = new UpdateCommentRequest("a");

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void updateCommentRequest_validation_shouldPassForMaxLengthText() {
        String maxLengthText = "a".repeat(2000);
        UpdateCommentRequest validRequest = new UpdateCommentRequest(maxLengthText);

        Set<ConstraintViolation<UpdateCommentRequest>> violations = validator.validate(validRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void updateCommentRequest_equalsAndHashCode_shouldWorkCorrectly() {
        UpdateCommentRequest request1 = new UpdateCommentRequest("Test");
        UpdateCommentRequest request2 = new UpdateCommentRequest("Test");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    void updateCommentRequest_toString_shouldReturnStringRepresentation() {
        UpdateCommentRequest request = new UpdateCommentRequest("Test");

        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Test"));
    }
}