package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDtoTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        UserDto user = new UserDto(1L, "John Doe", "john@example.com");

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");

        assertEquals(2L, user.getId());
        assertEquals("Jane Doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
    }
}
