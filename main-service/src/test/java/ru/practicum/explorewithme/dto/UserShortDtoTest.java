package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserShortDtoTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UserShortDto user = new UserShortDto(1L, "Short Name");

        assertEquals(1L, user.getId());
        assertEquals("Short Name", user.getName());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UserShortDto user = new UserShortDto();
        user.setId(2L);
        user.setName("Another Name");

        assertEquals(2L, user.getId());
        assertEquals("Another Name", user.getName());
    }
}
