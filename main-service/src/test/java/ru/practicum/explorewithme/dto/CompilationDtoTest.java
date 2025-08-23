package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompilationDtoTest {

    @Test
    void testCompilationDtoNoArgsConstructor() {
        CompilationDto compilation = new CompilationDto();

        assertNull(compilation.getId());
        assertNull(compilation.getEvents());
        assertNull(compilation.getPinned());
        assertNull(compilation.getTitle());
    }

    @Test
    void testCompilationDtoAllArgsConstructor() {
        EventShortDto event = new EventShortDto();
        List<EventShortDto> events = List.of(event);

        CompilationDto compilation = new CompilationDto(1L, events, true, "Test Title");

        assertEquals(1L, compilation.getId());
        assertEquals(events, compilation.getEvents());
        assertTrue(compilation.getPinned());
        assertEquals("Test Title", compilation.getTitle());
    }

    @Test
    void testCompilationDtoSettersAndGetters() {
        CompilationDto compilation = new CompilationDto();
        EventShortDto event = new EventShortDto();
        List<EventShortDto> events = List.of(event);

        compilation.setId(2L);
        compilation.setEvents(events);
        compilation.setPinned(false);
        compilation.setTitle("New Title");

        assertEquals(2L, compilation.getId());
        assertEquals(events, compilation.getEvents());
        assertFalse(compilation.getPinned());
        assertEquals("New Title", compilation.getTitle());
    }

    @Test
    void testCompilationDtoEqualsAndHashCode() {
        CompilationDto comp1 = new CompilationDto(1L, List.of(), true, "Title");
        CompilationDto comp2 = new CompilationDto(1L, List.of(), true, "Title");
        CompilationDto comp3 = new CompilationDto(2L, List.of(), false, "Different");

        assertEquals(comp1, comp2);
        assertNotEquals(comp1, comp3);
        assertEquals(comp1.hashCode(), comp2.hashCode());
    }

    @Test
    void testCompilationDtoToString() {
        CompilationDto compilation = new CompilationDto(1L, List.of(), true, "Test");
        String toString = compilation.toString();

        assertTrue(toString.contains("Test"));
        assertTrue(toString.contains("1"));
    }
}