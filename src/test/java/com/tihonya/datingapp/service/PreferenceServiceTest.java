package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.model.Preference;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.ProfileRepository;
import com.tihonya.datingapp.repository.PreferenceRepository;
import com.tihonya.datingapp.mapper.PreferenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreferenceServiceTest {

    @Mock
    private PreferenceRepository preferenceRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PreferenceMapper preferenceMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private PreferenceService preferenceService;

    private Preference preference;
    private PreferenceDto preferenceDto;
    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile();
        profile.setId(1L);

        preference = new Preference();
        preference.setId(1L);
        preference.setCategory("Music");
        preference.setValue("Rock");
        preference.setProfile(profile);

        preferenceDto = new PreferenceDto();
        preferenceDto.setId(1L);
        preferenceDto.setCategory("Music");
        preferenceDto.setValue("Rock");
        preferenceDto.setProfileId(1L);
    }

    @Test
    void testCreatePreference_whenValid_shouldSaveAndReturnDto() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(preferenceMapper.toEntity(preferenceDto)).thenReturn(preference);
        when(preferenceRepository.save(preference)).thenReturn(preference);
        when(preferenceMapper.toDto(preference)).thenReturn(preferenceDto);

        PreferenceDto result = preferenceService.createPreference(preferenceDto);

        assertEquals("Rock", result.getValue());
        verify(preferenceRepository).save(preference);
        verify(cacheService).clearCache("all_preferences");
    }

    @Test
    void testCreatePreference_whenProfileNotFound_shouldThrow() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> preferenceService.createPreference(preferenceDto));
        assertEquals("Profile not found", ex.getMessage());
    }

    @Test
    void testGetAllPreferences_shouldReturnAll() {
        List<Preference> preferences = List.of(preference);
        when(preferenceRepository.findAll()).thenReturn(preferences);
        when(preferenceMapper.toDto(preference)).thenReturn(preferenceDto);

        List<PreferenceDto> result = preferenceService.getAllPreferences();

        assertEquals(1, result.size());
        assertEquals("Rock", result.getFirst().getValue());
    }

    @Test
    void testGetPreferenceById_whenCached_shouldReturnFromCache() {
        when(cacheService.getFromCache("preference_1", PreferenceDto.class)).thenReturn(preferenceDto);

        PreferenceDto result = preferenceService.getPreferenceById(1L);

        assertEquals("Rock", result.getValue());
        verify(preferenceRepository, never()).findById(anyLong());
    }

    @Test
    void testGetPreferenceById_whenNotCached_shouldReturnFromDbAndCacheIt() {
        when(cacheService.getFromCache("preference_1", PreferenceDto.class)).thenReturn(null);
        when(preferenceRepository.findById(1L)).thenReturn(Optional.of(preference));
        when(preferenceMapper.toDto(preference)).thenReturn(preferenceDto);

        PreferenceDto result = preferenceService.getPreferenceById(1L);

        assertEquals("Rock", result.getValue());
        verify(cacheService).saveToCache("preference_1", preferenceDto);
    }

    @Test
    void testGetPreferenceById_whenNotFound_shouldThrow() {
        when(cacheService.getFromCache("preference_99", PreferenceDto.class)).thenReturn(null);
        when(preferenceRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> preferenceService.getPreferenceById(99L));
        assertEquals("Preference not found", ex.getMessage());
    }

    @Test
    void testUpdatePreference_whenValid_shouldUpdate() {
        when(preferenceRepository.findById(1L)).thenReturn(Optional.of(preference));
        when(preferenceRepository.save(preference)).thenReturn(preference);
        when(preferenceMapper.toDto(preference)).thenReturn(preferenceDto);

        PreferenceDto updated = preferenceService.updatePreference(1L, preferenceDto);

        assertEquals("Rock", updated.getValue());
        verify(preferenceRepository).save(preference);
        verify(cacheService).clearCache("preference_1");
    }

    @Test
    void testUpdatePreference_whenNotFound_shouldThrow() {
        when(preferenceRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> preferenceService.updatePreference(1L, preferenceDto));
        assertEquals("Preference not found", ex.getMessage());
    }

    @Test
    void testDeletePreference_whenValid_shouldDeleteAndClearCache() {
        when(preferenceRepository.findById(1L)).thenReturn(Optional.of(preference));

        preferenceService.deletePreference(1L);

        verify(preferenceRepository).delete(preference);
        verify(cacheService).clearCache("all_preferences");
        verify(cacheService).clearCache("preference_1");
    }

    @Test
    void testDeletePreference_whenNotFound_shouldThrow() {
        when(preferenceRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> preferenceService.deletePreference(1L));
        assertEquals("Preference not found", ex.getMessage());
    }
}
