package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.model.User;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Transactional
@Component
public class UserMapper {
    private final InterestMapper interestMapper;
    private final ProfileMapper profileMapper;

    public UserMapper(InterestMapper interestMapper, ProfileMapper profileMapper) {
        this.interestMapper = interestMapper;
        this.profileMapper = profileMapper;
    }

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword()); // Добавили маппинг пароля
        dto.setRole(user.getRole().name());  // Конвертируем Enum в String
        if (user.getProfile() != null) {
            dto.setProfile(profileMapper.toDto(user.getProfile()));
        }
        // Маппируем список интересов в InterestDto
        List<InterestDto> interestDtos = user.getInterests().stream()
                .map(interestMapper::toDto)  // Используем InterestMapper для преобразования интересов
                .collect(Collectors.toList());
        dto.setInterests(interestDtos);
        return dto;
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }
}

