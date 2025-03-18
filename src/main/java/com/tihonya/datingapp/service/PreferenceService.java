package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.mapper.PreferenceMapper;
import com.tihonya.datingapp.model.Preference;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.PreferenceRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {
    private final PreferenceRepository preferenceRepository;
    private final ProfileRepository profileRepository;
    private final PreferenceMapper preferenceMapper;

    @Transactional
    public PreferenceDto createPreference(PreferenceDto preferenceDto) {
        Preference preference = preferenceMapper.toEntity(preferenceDto);

        Profile profile = profileRepository.findById(preferenceDto.getProfileId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        preference.setProfile(profile);
        return preferenceMapper.toDto(preferenceRepository.save(preference));
    }

    public List<PreferenceDto> getAllPreferences() {
        return preferenceRepository.findAll()
                .stream()
                .map(preferenceMapper::toDto)
                .collect(Collectors.toList());
    }

    public PreferenceDto getPreferenceById(Long id) {
        Preference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
        return preferenceMapper.toDto(preference);
    }

    @Transactional
    public PreferenceDto updatePreference(Long id, PreferenceDto preferenceDto) {
        Preference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preference not found"));

        preference.setCategory(preferenceDto.getCategory());
        preference.setValue(preferenceDto.getValue());

        return preferenceMapper.toDto(preferenceRepository.save(preference));
    }

    @Transactional
    public void deletePreference(Long id) {
        Preference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Preference not found"));

        preferenceRepository.delete(preference);
    }
}

