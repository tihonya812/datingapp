package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.model.Preference;
import com.tihonya.datingapp.model.Profile;
import org.springframework.stereotype.Component;

@Component
public class PreferenceMapper {
    public Preference toEntity(PreferenceDto dto) {
        Preference preference = new Preference();
        preference.setId(dto.getId());
        preference.setCategory(dto.getCategory());
        preference.setValue(dto.getValue());
        if (dto.getProfileId() != null) {
            Profile profile = new Profile();
            profile.setId(dto.getProfileId());
            preference.setProfile(profile);
        }
        return preference;
    }

    public PreferenceDto toDto(Preference preference) {
        PreferenceDto dto = new PreferenceDto();
        dto.setId(preference.getId());
        dto.setCategory(preference.getCategory());
        dto.setValue(preference.getValue());
        dto.setProfileId(preference.getProfile() != null ? preference.getProfile().getId() : null);
        return dto;
    }
}
