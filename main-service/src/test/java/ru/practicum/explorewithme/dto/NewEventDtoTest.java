package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import ru.practicum.explorewithme.model.Location;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewEventDtoTest {

    private final Validator validator;

    public NewEventDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testNewEventDtoNoArgsConstructor() {
        NewEventDto event = new NewEventDto();

        assertNull(event.getAnnotation());
        assertNull(event.getCategory());
        assertNull(event.getDescription());
        assertNull(event.getEventDate());
        assertNull(event.getLocation());
        assertFalse(event.getPaid());
        assertEquals(0, event.getParticipantLimit());
        assertTrue(event.getRequestModeration());
        assertNull(event.getTitle());
    }

    @Test
    void testNewEventDtoAllArgsConstructor() {
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);
        Location location = new Location(59.9343f, 30.3351f);

        NewEventDto event = new NewEventDto(
                "Valid annotation that meets minimum length requirements",
                1L,
                "Valid description that meets minimum length requirements of 20 characters",
                eventDate,
                location,
                true,
                50,
                false,
                "Test Title"
        );

        assertEquals("Valid annotation that meets minimum length requirements", event.getAnnotation());
        assertEquals(1L, event.getCategory());
        assertEquals(
                "Valid description that meets minimum length requirements of 20 characters",
                event.getDescription()
        );
        assertEquals(eventDate, event.getEventDate());
        assertEquals(location, event.getLocation());
        assertTrue(event.getPaid());
        assertEquals(50, event.getParticipantLimit());
        assertFalse(event.getRequestModeration());
        assertEquals("Test Title", event.getTitle());
    }

    @Test
    void testNewEventDtoValidationValid() {
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);
        Location location = new Location(59.9343f, 30.3351f);

        NewEventDto event = new NewEventDto(
                "Valid annotation with sufficient length",
                1L,
                "Valid description with sufficient length to pass validation",
                eventDate,
                location,
                false,
                0,
                true,
                "Valid Title"
        );

        Set<ConstraintViolation<NewEventDto>> violations = validator.validate(event);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNewEventDtoValidationAnnotationTooShort() {
        NewEventDto event = new NewEventDto();
        event.setAnnotation("Short");
        event.setCategory(1L);
        event.setDescription("Valid description with sufficient length");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setLocation(new Location(59.9343f, 30.3351f));
        event.setTitle("Valid Title");

        Set<ConstraintViolation<NewEventDto>> violations = validator.validate(event);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.Size.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewEventDtoValidationNullCategory() {
        NewEventDto event = new NewEventDto();
        event.setAnnotation("Valid annotation with sufficient length");
        event.setCategory(null);
        event.setDescription("Valid description with sufficient length");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setLocation(new Location(59.9343f, 30.3351f));
        event.setTitle("Valid Title");

        Set<ConstraintViolation<NewEventDto>> violations = validator.validate(event);
        assertEquals(1, violations.size());
        assertEquals(jakarta.validation.constraints.NotNull.class,
                violations.iterator().next().getConstraintDescriptor().getAnnotation().annotationType());
    }

    @Test
    void testNewEventDtoSettersAndGetters() {
        NewEventDto event = new NewEventDto();
        LocalDateTime eventDate = LocalDateTime.now().plusDays(2);
        Location location = new Location(60.0f, 30.0f);

        event.setAnnotation("New annotation with sufficient length");
        event.setCategory(2L);
        event.setDescription("New description with sufficient length");
        event.setEventDate(eventDate);
        event.setLocation(location);
        event.setPaid(true);
        event.setParticipantLimit(100);
        event.setRequestModeration(false);
        event.setTitle("New Title");

        assertEquals("New annotation with sufficient length", event.getAnnotation());
        assertEquals(2L, event.getCategory());
        assertEquals("New description with sufficient length", event.getDescription());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(location, event.getLocation());
        assertTrue(event.getPaid());
        assertEquals(100, event.getParticipantLimit());
        assertFalse(event.getRequestModeration());
        assertEquals("New Title", event.getTitle());
    }

    @Test
    void testNewEventDtoEqualsAndHashCode() {
        LocalDateTime eventDate = LocalDateTime.now().plusDays(1);
        Location location = new Location(59.9343f, 30.3351f);

        NewEventDto event1 = new NewEventDto("Annotation", 1L, "Description",
                eventDate, location, false, 0, true, "Title");
        NewEventDto event2 = new NewEventDto("Annotation", 1L, "Description",
                eventDate, location, false, 0, true, "Title");
        NewEventDto event3 = new NewEventDto("Different", 2L, "Description",
                eventDate, location, false, 0, true, "Title");

        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void testNewEventDtoToString() {
        NewEventDto event = new NewEventDto("Test Annotation", 1L, "Test Description",
                null, null, false, 0, true, "Test Title");

        String toString = event.toString();
        assertTrue(toString.contains("Test"));
    }
}