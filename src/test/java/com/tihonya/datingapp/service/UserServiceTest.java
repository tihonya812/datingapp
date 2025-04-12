package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.enums.Role;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.UserMapper;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserService userService;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setPassword("password123");
        userDto.setRole("USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
        user.setRole(Role.USER);
    }

    @Test
    void testGetUserById_whenUserExists_shouldReturnDto() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setPassword("hashed");
        user.setRole(Role.USER);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setUsername("john");
        userDto.setEmail("john@example.com");
        userDto.setRole("USER");

        when(cacheService.getFromCache("user_" + userId, UserDto.class)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId);

        assertThat(result.getUsername()).isEqualTo("john");
        verify(cacheService).saveToCache("user_" + userId, userDto);
    }

    @Test
    void testGetUserById_whenUserNotFound_shouldThrowException() {
        Long userId = 42L;
        when(cacheService.getFromCache("user_" + userId, UserDto.class)).thenReturn(null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void testCreateUser_whenUserIsValid_shouldReturnDto() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto created = userService.createUser(userDto);

        assertEquals(userDto.getUsername(), created.getUsername());
        assertEquals(userDto.getEmail(), created.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUser_whenEmailAlreadyExists_shouldThrowException() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testCreateUser_whenUsernameAlreadyExists_shouldThrowException() {
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testUpdateUser_whenValid_shouldReturnUpdatedDto() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto updated = userService.updateUser(id, userDto);

        assertEquals(userDto.getUsername(), updated.getUsername());
        assertEquals(userDto.getEmail(), updated.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUser_whenUserNotFound_shouldThrowException() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(id, userDto));
    }

    @Test
    void testUpdateUser_whenEmailTakenByAnother_shouldThrowException() {
        Long id = 1L;
        user.setEmail("another@example.com");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(id, userDto));
    }

    @Test
    void testUpdateUser_whenUsernameTakenByAnother_shouldThrowException() {
        Long id = 1L;
        user.setUsername("anotherUser");
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(id, userDto));
    }

    @Test
    void testDeleteUser_whenExists_shouldDelete() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void testDeleteUser_whenNotFound_shouldThrowException() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(id));
    }
}
