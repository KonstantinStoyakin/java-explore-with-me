package ru.practicum.explorewithme.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.dto.UserShortDto;
import ru.practicum.explorewithme.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toUser_shouldMapNewUserRequestToUser() {
        NewUserRequest request = new NewUserRequest("test@mail.com", "John");
        User user = mapper.toUser(request);

        assertNotNull(user);
        assertEquals("John", user.getName());
        assertEquals("test@mail.com", user.getEmail());
    }

    @Test
    void toUserDto_shouldMapUserToUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");
        user.setEmail("alice@mail.com");

        UserDto dto = mapper.toUserDto(user);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("alice@mail.com", dto.getEmail());
    }

    @Test
    void toUserShortDto_shouldMapUserToUserShortDto() {
        User user = new User();
        user.setId(2L);
        user.setName("Bob");

        UserShortDto dto = mapper.toUserShortDto(user);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("Bob", dto.getName());
    }
}
