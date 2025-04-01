package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.PreferenceMapper;
import com.tihonya.datingapp.model.Preference;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.PreferenceRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {
    private static final String PROFILE_NOT_FOUND = "Profile not found";
    private static final String PREFERENCE_NOT_FOUND = "Preference not found";
    private static final String CACHE_KEY_PREFERENCES = "all_preferences";

    private final PreferenceRepository preferenceRepository;
    private final ProfileRepository profileRepository;
    private final PreferenceMapper preferenceMapper;
    private final CacheService cacheService;

    @Transactional
    public PreferenceDto createPreference(PreferenceDto preferenceDto) {
        Preference preference = preferenceMapper.toEntity(preferenceDto);

        Profile profile = profileRepository.findById(preferenceDto.getProfileId())
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        preference.setProfile(profile);
        clearPreferenceCache();
        return preferenceMapper.toDto(preferenceRepository.save(preference));
    }

    public List<PreferenceDto> getAllPreferences() {
        List<PreferenceDto> cachedPreferences = cacheService.getFromCache(CACHE_KEY_PREFERENCES, List.class);
        if (cachedPreferences != null) {
            return cachedPreferences;
        }

        List<PreferenceDto> preferences = preferenceRepository.findAll()
                .stream()
                .map(preferenceMapper::toDto)
                .toList();
        cacheService.saveToCache(CACHE_KEY_PREFERENCES, preferences);
        return preferences;
    }

    public PreferenceDto getPreferenceById(Long id) {
        String cacheKey = "preference_" + id;
        PreferenceDto cachedPreference = cacheService.getFromCache(cacheKey, PreferenceDto.class);
        if (cachedPreference != null) {
            return cachedPreference;
        }

        Preference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PREFERENCE_NOT_FOUND));
        PreferenceDto preferenceDto = preferenceMapper.toDto(preference);
        cacheService.saveToCache(cacheKey, preferenceDto);
        return preferenceDto;
    }

    public void clearPreferenceCache(Long id) {
        cacheService.clearCache("preference_" + id);
    }

    public void clearPreferenceCache() {
        cacheService.clearCache(CACHE_KEY_PREFERENCES);
    }

    @Transactional
    public PreferenceDto updatePreference(Long id, PreferenceDto preferenceDto) {
        Preference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PREFERENCE_NOT_FOUND));

        preference.setCategory(preferenceDto.getCategory());
        preference.setValue(preferenceDto.getValue());

        clearPreferenceCache(id);
        return preferenceMapper.toDto(preferenceRepository.save(preference));
    }

    @Transactional
    public void deletePreference(Long id) {
        Preference preference = preferenceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PREFERENCE_NOT_FOUND));

        preferenceRepository.delete(preference);
        clearPreferenceCache();
        clearPreferenceCache(id);
    }
}

