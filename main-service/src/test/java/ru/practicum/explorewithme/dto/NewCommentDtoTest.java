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

class NewCommentDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void newCommentDto_shouldHaveCorrectStructure() {
        NewCommentDto dto = new NewCommentDto("Test comment", 1L);

        assertEquals("Test comment", dto.getText());
        assertEquals(1L, dto.getEventId());
    }

    @Test
    void newCommentDto_noArgsConstructor_shouldCreateEmptyObject() {
        NewCommentDto dto = new NewCommentDto();

        assertNotNull(dto);
        assertNull(dto.getText());
        assertNull(dto.getEventId());
    }

    @Test
    void newCommentDto_settersAndGetters_shouldWorkCorrectly() {
        NewCommentDto dto = new NewCommentDto();

        dto.setText("Test comment");
        dto.setEventId(1L);

        assertEquals("Test comment", dto.getText());
        assertEquals(1L, dto.getEventId());
    }

    @Test
    void newCommentDto_validation_shouldPassForValidData() {
        NewCommentDto validDto = new NewCommentDto("Valid comment", 1L);

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(validDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void newCommentDto_validation_shouldDetectBlankText() {
        NewCommentDto invalidDto = new NewCommentDto("", 1L);

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(invalidDto);

        assertTrue(violations.size() >= 1);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("text")));
    }

    @Test
    void newCommentDto_validation_shouldDetectNullText() {
        NewCommentDto invalidDto = new NewCommentDto(null, 1L);

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(invalidDto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("text")));
    }

    @Test
    void newCommentDto_validation_shouldDetectNullEventId() {
        NewCommentDto invalidDto = new NewCommentDto("Test comment", null);

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(invalidDto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("eventId")));
    }

    @Test
    void newCommentDto_validation_shouldDetectTextTooLong() {
        String longText = "a".repeat(2001);
        NewCommentDto invalidDto = new NewCommentDto(longText, 1L);

        Set<ConstraintViolation<NewCommentDto>> violations = validator.validate(invalidDto);

        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("text")));
    }

    @Test
    void newCommentDto_equalsAndHashCode_shouldWorkCorrectly() {
        NewCommentDto dto1 = new NewCommentDto("Test", 1L);
        NewCommentDto dto2 = new NewCommentDto("Test", 1L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void newCommentDto_toString_shouldReturnStringRepresentation() {
        NewCommentDto dto = new NewCommentDto("Test", 1L);

        String toString = dto.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("1"));
    }
}