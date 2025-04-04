package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.enums.Role;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.UserMapper;
import com.tihonya.datingapp.model.Interest;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.InterestRepository;
import com.tihonya.datingapp.repository.UserRepository;
import com.tihonya.datingapp.util.HashUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String INTEREST_NOT_FOUND = "Interest not found";
    private static final String USER_NOT_FOUND = "User not found";
    private static final String CACHE_KEY_USERS = "all_users";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final InterestRepository interestRepository;
    private final CacheService cacheService;

    @Transactional
    public List<UserDto> getAllUsers() {
        List<UserDto> cachedUsers = cacheService.getFromCache(CACHE_KEY_USERS, List.class);
        if (cachedUsers != null) {
            return cachedUsers;
        }

        List<UserDto> users = userMapper.toDtoList(userRepository.findAll());
        cacheService.saveToCache(CACHE_KEY_USERS, users);
        return users;
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
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(HashUtil.hashPassword(userDto.getPassword())); // Хешируем пароль
        user.setRole(Role.valueOf(userDto.getRole())); // Преобразуем строку в Enum
        clearUserCache();
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException(USER_NOT_FOUND));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(HashUtil.hashPassword(userDto.getPassword()));
            // Хешируем только если передан новый пароль
        }

        user.setRole(Role.valueOf(userDto.getRole())); // Обновляем роль
        clearUserCache();
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
        clearUserCache();
    }

    @Transactional
    public UserDto addInterestToUser(Long userId, Long interestId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException(USER_NOT_FOUND));
        Interest interest = interestRepository.findById(interestId).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));

        user.getInterests().add(interest); // Добавляем интерес пользователю
        userRepository.save(user); // Сохраняем пользователя с новым интересом

        clearUserCache();
        return userMapper.toDto(user); // Возвращаем обновленного пользователя
    }
}

