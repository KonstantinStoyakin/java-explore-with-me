package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.model.Location;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateEventUserRequestTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Location loc = new Location(5.0f, 6.0f);
        LocalDateTime date = LocalDateTime.now();

        UpdateEventUserRequest event = new UpdateEventUserRequest(
                "Annotation text....",
                10L,
                "Description text....",
                date,
                loc,
                true,
                200,
                false,
                "SEND_TO_REVIEW",
                "User Event Title"
        );

        assertEquals("Annotation text....", event.getAnnotation());
        assertEquals(10L, event.getCategory());
        assertEquals("Description text....", event.getDescription());
        assertEquals(date, event.getEventDate());
        assertEquals(loc, event.getLocation());
        assertTrue(event.getPaid());
        assertEquals(200, event.getParticipantLimit());
        assertFalse(event.getRequestModeration());
        assertEquals("SEND_TO_REVIEW", event.getStateAction());
        assertEquals("User Event Title", event.getTitle());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UpdateEventUserRequest event = new UpdateEventUserRequest();
        Location loc = new Location(7.0f, 8.0f);
        LocalDateTime date = LocalDateTime.now();

        event.setAnnotation("New Annotation");
        event.setCategory(20L);
        event.setDescription("New Description");
        event.setEventDate(date);
        event.setLocation(loc);
        event.setPaid(false);
        event.setParticipantLimit(150);
        event.setRequestModeration(true);
        event.setStateAction("CANCEL_REVIEW");
        event.setTitle("Another Title");

        assertEquals("New Annotation", event.getAnnotation());
        assertEquals(20L, event.getCategory());
        assertEquals("New Description", event.getDescription());
        assertEquals(date, event.getEventDate());
        assertEquals(loc, event.getLocation());
        assertFalse(event.getPaid());
        assertEquals(150, event.getParticipantLimit());
        assertTrue(event.getRequestModeration());
        assertEquals("CANCEL_REVIEW", event.getStateAction());
        assertEquals("Another Title", event.getTitle());
    }
}
