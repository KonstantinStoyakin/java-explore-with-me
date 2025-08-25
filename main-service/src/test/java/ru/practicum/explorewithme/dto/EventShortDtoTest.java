package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventShortDtoTest {

    @Test
    void testEventShortDtoNoArgsConstructor() {
        EventShortDto event = new EventShortDto();

        assertNull(event.getAnnotation());
        assertNull(event.getCategory());
        assertNull(event.getConfirmedRequests());
        assertNull(event.getEventDate());
        assertNull(event.getId());
        assertNull(event.getInitiator());
        assertNull(event.getPaid());
        assertNull(event.getTitle());
        assertNull(event.getViews());
    }

    @Test
    void testEventShortDtoAllArgsConstructor() {
        LocalDateTime eventDate = LocalDateTime.now();
        CategoryDto category = new CategoryDto(1L, "Category");
        UserShortDto initiator = new UserShortDto(1L, "User");

        EventShortDto event = new EventShortDto(
                "Annotation", category, 5, eventDate, 1L, initiator, true, "Title", 10L
        );

        assertEquals("Annotation", event.getAnnotation());
        assertEquals(category, event.getCategory());
        assertEquals(5, event.getConfirmedRequests());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(1L, event.getId());
        assertEquals(initiator, event.getInitiator());
        assertTrue(event.getPaid());
        assertEquals("Title", event.getTitle());
        assertEquals(10L, event.getViews());
    }

    @Test
    void testEventShortDtoSettersAndGetters() {
        EventShortDto event = new EventShortDto();
        LocalDateTime eventDate = LocalDateTime.now();
        CategoryDto category = new CategoryDto(2L, "New Category");
        UserShortDto initiator = new UserShortDto(2L, "New User");

        event.setAnnotation("New Annotation");
        event.setCategory(category);
        event.setConfirmedRequests(10);
        event.setEventDate(eventDate);
        event.setId(2L);
        event.setInitiator(initiator);
        event.setPaid(false);
        event.setTitle("New Title");
        event.setViews(20L);

        assertEquals("New Annotation", event.getAnnotation());
        assertEquals(category, event.getCategory());
        assertEquals(10, event.getConfirmedRequests());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(2L, event.getId());
        assertEquals(initiator, event.getInitiator());
        assertFalse(event.getPaid());
        assertEquals("New Title", event.getTitle());
        assertEquals(20L, event.getViews());
    }

    @Test
    void testEventShortDtoEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        EventShortDto event1 = new EventShortDto("Annotation", null, 0, now,
                1L, null, false, "Title", 0L);
        EventShortDto event2 = new EventShortDto("Annotation", null, 0, now,
                1L, null, false, "Title", 0L);
        EventShortDto event3 = new EventShortDto("Different", null, 0, now,
                1L, null, false, "Title", 0L);

        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void testEventShortDtoToString() {
        EventShortDto event = new EventShortDto("Test", null, 0, null,
                1L, null, false, "Title", 0L);

        String toString = event.toString();
        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("Title"));
    }
}