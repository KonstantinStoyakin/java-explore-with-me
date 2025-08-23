package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewCompilationDtoTest {

    private final Validator validator;

    public NewCompilationDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNewCompilationDtoNoArgsConstructor() {
        NewCompilationDto compilation = new NewCompilationDto();

        assertNull(compilation.getEvents());
        assertFalse(compilation.getPinned());
        assertNull(compilation.getTitle());
    }

    @Test
    void testNewCompilationDtoAllArgsConstructor() {
        List<Long> events = List.of(1L, 2L);

        NewCompilationDto compilation = new NewCompilationDto(events, true, "Test Title");

        assertEquals(events, compilation.getEvents());
        assertTrue(compilation.getPinned());
        assertEquals("Test Title", compilation.getTitle());
    }

    @Test
    void testNewCompilationDtoDefaultPinnedValue() {
        NewCompilationDto compilation = new NewCompilationDto();
        assertFalse(compilation.getPinned());
    }

    @Test
    void testNewCompilationDtoValidationValid() {
        NewCompilationDto compilation = new NewCompilationDto(
                List.of(1L), false, "Valid Title"
        );

        Set<ConstraintViolation<NewCompilationDto>> violations = validator.validate(compilation);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNewCompilationDtoValidationBlankTitle() {
        NewCompilationDto compilation = new NewCompilationDto(List.of(1L), false, "   ");
        Set<ConstraintViolation<NewCompilationDto>> violations = validator.validate(compilation);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotBlank.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewCompilationDtoValidationNullTitle() {
        NewCompilationDto compilation = new NewCompilationDto(
                List.of(1L), false, null
        );

        Set<ConstraintViolation<NewCompilationDto>> violations = validator.validate(compilation);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotBlank.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewCompilationDtoValidationTitleTooLong() {
        String longTitle = "A".repeat(51);
        NewCompilationDto compilation = new NewCompilationDto(List.of(1L), false, longTitle);
        Set<ConstraintViolation<NewCompilationDto>> violations = validator.validate(compilation);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.Size.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewCompilationDtoSettersAndGetters() {
        NewCompilationDto compilation = new NewCompilationDto();
        List<Long> events = List.of(3L, 4L);

        compilation.setEvents(events);
        compilation.setPinned(true);
        compilation.setTitle("New Title");

        assertEquals(events, compilation.getEvents());
        assertTrue(compilation.getPinned());
        assertEquals("New Title", compilation.getTitle());
    }

    @Test
    void testNewCompilationDtoEqualsAndHashCode() {
        NewCompilationDto comp1 = new NewCompilationDto(List.of(1L), true, "Title");
        NewCompilationDto comp2 = new NewCompilationDto(List.of(1L), true, "Title");
        NewCompilationDto comp3 = new NewCompilationDto(List.of(2L), false, "Different");

        assertEquals(comp1, comp2);
        assertNotEquals(comp1, comp3);
        assertEquals(comp1.hashCode(), comp2.hashCode());
    }

    @Test
    void testNewCompilationDtoToString() {
        NewCompilationDto compilation = new NewCompilationDto(List.of(1L), true, "Test");
        String toString = compilation.toString();

        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("1"));
    }
}