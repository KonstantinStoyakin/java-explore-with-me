package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateCompilationRequestTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UpdateCompilationRequest compilation = new UpdateCompilationRequest(
                List.of(1L, 2L), true, "New Compilation"
        );

        assertEquals(List.of(1L, 2L), compilation.getEvents());
        assertTrue(compilation.getPinned());
        assertEquals("New Compilation", compilation.getTitle());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UpdateCompilationRequest compilation = new UpdateCompilationRequest();
        compilation.setEvents(List.of(5L, 6L));
        compilation.setPinned(false);
        compilation.setTitle("Another Title");

        assertEquals(List.of(5L, 6L), compilation.getEvents());
        assertFalse(compilation.getPinned());
        assertEquals("Another Title", compilation.getTitle());
    }
}
