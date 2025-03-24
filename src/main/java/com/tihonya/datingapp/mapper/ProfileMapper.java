package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.model.Profile;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Transactional
@Component
public class ProfileMapper {
    private final PreferenceMapper preferenceMapper;
    private final PhotoMapper photoMapper;

    // Конструктор для инъекции зависимости PreferenceMapper
    public ProfileMapper(PreferenceMapper preferenceMapper, PhotoMapper photoMapper) {
        this.preferenceMapper = preferenceMapper;
        this.photoMapper = photoMapper;
    }

    public ProfileDto toDto(Profile profile) {
        ProfileDto dto = new ProfileDto();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setAge(profile.getAge());
        dto.setCity(profile.getCity());
        dto.setBio(profile.getBio());
        dto.setUserId(profile.getUser().getId()); // Добавляем связь с User
        // Преобразуем предпочтения в PreferenceDto
        List<PreferenceDto> preferenceDtos = profile.getPreferences().stream()
                .map(preferenceMapper::toDto)  // Маппируем Preference на PreferenceDto
                .collect(Collectors.toList());
        dto.setPreferences(preferenceDtos);

        // Преобразуем фото в PhotoDto
        List<PhotoDto> photoDtos = profile.getPhotos().stream()
                .map(photoMapper::toDto)
                .collect(Collectors.toList());
        dto.setPhotos(photoDtos);
        return dto;
    }

    public List<ProfileDto> toDtoList(List<Profile> profiles) {
        return profiles.stream().map(this::toDto).toList();
    }
}



