package ru.practicum.explorewithme.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testGettersSettersAndBuilder() {
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");

        User built = User.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        assertThat(built.getId()).isEqualTo(2L);
        assertThat(built.getName()).isEqualTo("Jane Doe");
        assertThat(built.getEmail()).isEqualTo("jane@example.com");
    }
}
