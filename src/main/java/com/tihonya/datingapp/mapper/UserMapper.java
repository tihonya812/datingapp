package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.model.User;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Component;

@Transactional
@Component
public class UserMapper {
    private final ProfileMapper profileMapper;

    public UserMapper(ProfileMapper profileMapper) {
        this.profileMapper = profileMapper;
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword()); // Добавили маппинг пароля
        dto.setRole(user.getRole().name());  // Конвертируем Enum в String
        if (user.getProfile() != null) {
            dto.setProfile(profileMapper.toDto(user.getProfile()));
        }
        return dto;
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}

