package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.model.Location;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventFullDtoTest {

    @Test
    void testEventFullDtoNoArgsConstructor() {
        EventFullDto event = new EventFullDto();

        assertNull(event.getId());
        assertNull(event.getAnnotation());
        assertNull(event.getCategory());
        assertNull(event.getConfirmedRequests());
        assertNull(event.getCreatedOn());
        assertNull(event.getDescription());
        assertNull(event.getEventDate());
        assertNull(event.getInitiator());
        assertNull(event.getLocation());
        assertNull(event.getPaid());
        assertNull(event.getParticipantLimit());
        assertNull(event.getPublishedOn());
        assertNull(event.getRequestModeration());
        assertNull(event.getState());
        assertNull(event.getTitle());
        assertNull(event.getViews());
    }

    @Test
    void testEventFullDtoAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        CategoryDto category = new CategoryDto(1L, "Category");
        UserShortDto initiator = new UserShortDto(1L, "User");
        Location location = new Location(59.9343f, 30.3351f);

        EventFullDto event = new EventFullDto(
                1L, "Annotation", category, 10, now, "Description",
                now.plusDays(1), initiator, location, true, 100,
                now.plusHours(1), false, "PUBLISHED", "Title", 50L
        );

        assertEquals(1L, event.getId());
        assertEquals("Annotation", event.getAnnotation());
        assertEquals(category, event.getCategory());
        assertEquals(10, event.getConfirmedRequests());
        assertEquals(now, event.getCreatedOn());
        assertEquals("Description", event.getDescription());
        assertEquals(now.plusDays(1), event.getEventDate());
        assertEquals(initiator, event.getInitiator());
        assertEquals(location, event.getLocation());
        assertTrue(event.getPaid());
        assertEquals(100, event.getParticipantLimit());
        assertEquals(now.plusHours(1), event.getPublishedOn());
        assertFalse(event.getRequestModeration());
        assertEquals("PUBLISHED", event.getState());
        assertEquals("Title", event.getTitle());
        assertEquals(50L, event.getViews());
    }

    @Test
    void testEventFullDtoSettersAndGetters() {
        EventFullDto event = new EventFullDto();
        LocalDateTime now = LocalDateTime.now();
        CategoryDto category = new CategoryDto(1L, "Category");
        UserShortDto initiator = new UserShortDto(1L, "User");
        Location location = new Location(59.9343f, 30.3351f);

        event.setId(2L);
        event.setAnnotation("New Annotation");
        event.setCategory(category);
        event.setConfirmedRequests(5);
        event.setCreatedOn(now);
        event.setDescription("New Description");
        event.setEventDate(now.plusDays(2));
        event.setInitiator(initiator);
        event.setLocation(location);
        event.setPaid(false);
        event.setParticipantLimit(50);
        event.setPublishedOn(now.plusHours(2));
        event.setRequestModeration(true);
        event.setState("PENDING");
        event.setTitle("New Title");
        event.setViews(25L);

        assertEquals(2L, event.getId());
        assertEquals("New Annotation", event.getAnnotation());
        assertEquals(category, event.getCategory());
        assertEquals(5, event.getConfirmedRequests());
        assertEquals(now, event.getCreatedOn());
        assertEquals("New Description", event.getDescription());
        assertEquals(now.plusDays(2), event.getEventDate());
        assertEquals(initiator, event.getInitiator());
        assertEquals(location, event.getLocation());
        assertFalse(event.getPaid());
        assertEquals(50, event.getParticipantLimit());
        assertEquals(now.plusHours(2), event.getPublishedOn());
        assertTrue(event.getRequestModeration());
        assertEquals("PENDING", event.getState());
        assertEquals("New Title", event.getTitle());
        assertEquals(25L, event.getViews());
    }

    @Test
    void testEventFullDtoEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        EventFullDto event1 = new EventFullDto(1L, "Annotation", null, 0, now,
                "Description", now, null, null, false, 0, null, true, "PENDING", "Title", 0L);
        EventFullDto event2 = new EventFullDto(1L, "Annotation", null, 0, now,
                "Description", now, null, null, false, 0, null, true, "PENDING", "Title", 0L);
        EventFullDto event3 = new EventFullDto(2L, "Different", null, 0, now,
                "Description", now, null, null, false, 0, null, true, "PENDING", "Title", 0L);

        assertEquals(event1, event2);
        assertNotEquals(event1, event3);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void testEventFullDtoToString() {
        EventFullDto event = new EventFullDto(1L, "Test", null, 0, null,
                "Desc", null, null, null, false, 0, null, true, "STATE", "Title", 0L);

        String toString = event.toString();
        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("Title"));
    }
}