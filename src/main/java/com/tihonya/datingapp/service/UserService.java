package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.enums.Role;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.UserMapper;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String CACHE_KEY_USERS = "all_users";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheService cacheService;
    private final PasswordEncoder passwordEncoder;
    private final ProfileService profileService;

    @Transactional
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional
    public UserDto getUserById(Long id) {
        String cacheKey = "user_" + id;
        UserDto cachedUser = cacheService.getFromCache(cacheKey, UserDto.class);
        if (cachedUser != null) {
            return cachedUser;
        }

        User user = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException(USER_NOT_FOUND));
        UserDto userDto = userMapper.toDto(user);
        cacheService.saveToCache(cacheKey, userDto);
        return userDto;
    }

    public void clearUserCache() {
        cacheService.clearCache(CACHE_KEY_USERS);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        // Проверяем, существует ли уже пользователь с таким email
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Этот email уже занят.");
        }

        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Этот Логин уже занят.");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(Role.valueOf(userDto.getRole())); // Преобразуем строку в Enum
        clearUserCache();
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException(USER_NOT_FOUND));

        // Проверяем уникальность email, исключая текущего пользователя
        if (!user.getEmail().equals(userDto.getEmail()) &&
                userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Этот email уже занят.");
        }

        // Проверяем уникальность логина, исключая текущего пользователя
        if (!user.getUsername().equals(userDto.getUsername()) &&
                userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Этот Логин уже занят.");
        }

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        user.setRole(Role.valueOf(userDto.getRole())); // Обновляем роль
        clearUserCache();
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        // Удаляем профиль, если он существует
        Profile profile = user.getProfile();
        if (profile != null) {
            profileService.deleteProfile(profile.getId());
        }

        userRepository.deleteById(id);
        clearUserCache();
    }

    @Transactional
    public UserDto register(UserDto userDto) {
        return createUser(userDto);
    }

    @Transactional
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole().name());
        Profile profile = user.getProfile();
        if (profile != null) {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setId(profile.getId());
            profileDto.setName(profile.getName());
            profileDto.setAge(profile.getAge());
            profileDto.setCity(profile.getCity());
            profileDto.setBio(profile.getBio());
            profileDto.setUserId(profile.getUser() != null ? profile.getUser().getId() : null);
            profileDto.setPhotos(profile.getPhotos().stream()
                    .map(p -> {
                        PhotoDto photoDto = new PhotoDto();
                        photoDto.setId(p.getId());
                        photoDto.setUrl(p.getUrl());
                        return photoDto;
                    })
                    .collect(Collectors.toList()));
            profileDto.setPreferences(profile.getPreferences().stream()
                    .map(p -> {
                        PreferenceDto preferenceDto = new PreferenceDto();
                        preferenceDto.setId(p.getId());
                        preferenceDto.setCategory(p.getCategory());
                        preferenceDto.setValue(p.getValue());
                        return preferenceDto;
                    })
                    .collect(Collectors.toList()));
            profileDto.setInterests(null); // Избегаем маппинга interests
            userDto.setProfile(profileDto);
        }
        return userDto;
    }
}
