package ru.practicum.explorewithme.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewUserRequestTest {

    @Test
    void testAllArgsConstructorAndGetters() {
        NewUserRequest user = new NewUserRequest("test@example.com", "John Doe");

        assertEquals("test@example.com", user.getEmail());
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        NewUserRequest user = new NewUserRequest();
        user.setEmail("hello@test.com");
        user.setName("Jane Doe");

        assertEquals("hello@test.com", user.getEmail());
        assertEquals("Jane Doe", user.getName());
    }
}
