package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.dto.NewUserRequest;
import java.util.List;

public interface UserService {
    UserDto registerUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}