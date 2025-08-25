package ru.practicum.explorewithme.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.mapper.UserMapper;
import ru.practicum.explorewithme.model.User;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_shouldCreateNewUser() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setName("Test User");
        newUserRequest.setEmail("test@email.com");

        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        UserDto expectedDto = new UserDto();
        expectedDto.setId(1L);
        expectedDto.setName("Test User");
        expectedDto.setEmail("test@email.com");

        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userMapper.toUser(newUserRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDto(user)).thenReturn(expectedDto);

        UserDto result = userService.registerUser(newUserRequest);

        assertNotNull(result);
        assertEquals(expectedDto.getId(), result.getId());
        assertEquals(expectedDto.getName(), result.getName());
        verify(userRepository).existsByEmail("test@email.com");
        verify(userRepository).save(user);
    }

    @Test
    void registerUser_shouldThrowConflictExceptionForDuplicateEmail() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setEmail("duplicate@email.com");

        when(userRepository.existsByEmail("duplicate@email.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.registerUser(newUserRequest));
        verify(userRepository).existsByEmail("duplicate@email.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_shouldThrowNotFoundException() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).existsById(userId);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getUsers_shouldReturnAllUsersWhenIdsIsNull() {
        List<Long> ids = null;
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").ascending());

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@email.com");

        Page<User> userPage = new PageImpl<>(List.of(user1, user2));

        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("User 1");
        dto1.setEmail("user1@email.com");

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("User 2");
        dto2.setEmail("user2@email.com");

        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> result = userService.getUsers(ids, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1.getId(), result.get(0).getId());
        assertEquals(dto2.getId(), result.get(1).getId());
        verify(userRepository).findAll(pageRequest);
        verify(userRepository, never()).findAllByIdIn(any(), any());
    }

    @Test
    void getUsers_shouldReturnUsersByIds() {
        List<Long> ids = List.of(1L, 2L);
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").ascending());

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@email.com");

        List<User> users = List.of(user1, user2);

        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("User 1");
        dto1.setEmail("user1@email.com");

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("User 2");
        dto2.setEmail("user2@email.com");

        when(userRepository.findAllByIdIn(ids, pageRequest)).thenReturn(users);
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> result = userService.getUsers(ids, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto1.getId(), result.get(0).getId());
        assertEquals(dto2.getId(), result.get(1).getId());
        verify(userRepository).findAllByIdIn(ids, pageRequest);
        verify(userRepository, never()).findAll((Pageable) any());
    }

    @Test
    void getUsers_shouldHandleEmptyIds() {
        List<Long> ids = List.of();
        Integer from = 0;
        Integer size = 10;
        PageRequest pageRequest = PageRequest.of(0, size, Sort.by("id").ascending());

        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        user1.setEmail("user1@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@email.com");

        Page<User> userPage = new PageImpl<>(List.of(user1, user2));

        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("User 1");
        dto1.setEmail("user1@email.com");

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("User 2");
        dto2.setEmail("user2@email.com");

        when(userRepository.findAll(pageRequest)).thenReturn(userPage);
        when(userMapper.toUserDto(user1)).thenReturn(dto1);
        when(userMapper.toUserDto(user2)).thenReturn(dto2);

        List<UserDto> result = userService.getUsers(ids, from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll(pageRequest);
        verify(userRepository, never()).findAllByIdIn(any(), any());
    }

    @Test
    void getUsers_shouldHandlePagination() {
        List<Long> ids = null;
        Integer from = 10;
        Integer size = 5;
        PageRequest pageRequest = PageRequest.of(2, size, Sort.by("id").ascending());
        Page<User> userPage = new PageImpl<>(List.of());

        when(userRepository.findAll(pageRequest)).thenReturn(userPage);

        List<UserDto> result = userService.getUsers(ids, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll(pageRequest);
    }
}