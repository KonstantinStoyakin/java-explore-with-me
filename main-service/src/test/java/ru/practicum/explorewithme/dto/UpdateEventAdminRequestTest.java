package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.model.Location;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateEventAdminRequestTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        Location loc = new Location(1.0f, 2.0f);
        LocalDateTime date = LocalDateTime.now();

        UpdateEventAdminRequest event = new UpdateEventAdminRequest(
                "Long annotation text here....",
                1L,
                "Long description here....",
                date,
                loc,
                true,
                100,
                true,
                "PUBLISH",
                "Event Title"
        );

        assertEquals("Long annotation text here....", event.getAnnotation());
        assertEquals(1L, event.getCategory());
        assertEquals("Long description here....", event.getDescription());
        assertEquals(date, event.getEventDate());
        assertEquals(loc, event.getLocation());
        assertTrue(event.getPaid());
        assertEquals(100, event.getParticipantLimit());
        assertTrue(event.getRequestModeration());
        assertEquals("PUBLISH", event.getStateAction());
        assertEquals("Event Title", event.getTitle());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UpdateEventAdminRequest event = new UpdateEventAdminRequest();
        Location loc = new Location(3.0f, 4.0f);
        LocalDateTime date = LocalDateTime.now();

        event.setAnnotation("Annotation");
        event.setCategory(2L);
        event.setDescription("Description");
        event.setEventDate(date);
        event.setLocation(loc);
        event.setPaid(false);
        event.setParticipantLimit(50);
        event.setRequestModeration(false);
        event.setStateAction("CANCEL");
        event.setTitle("Title");

        assertEquals("Annotation", event.getAnnotation());
        assertEquals(2L, event.getCategory());
        assertEquals("Description", event.getDescription());
        assertEquals(date, event.getEventDate());
        assertEquals(loc, event.getLocation());
        assertFalse(event.getPaid());
        assertEquals(50, event.getParticipantLimit());
        assertFalse(event.getRequestModeration());
        assertEquals("CANCEL", event.getStateAction());
        assertEquals("Title", event.getTitle());
    }
}
