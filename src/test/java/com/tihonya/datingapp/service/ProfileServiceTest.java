package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.ProfileMapper;
import com.tihonya.datingapp.model.Interest;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.InterestRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import com.tihonya.datingapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileMapper profileMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private ProfileService profileService;

    private Profile profile;
    private ProfileDto profileDto;
    private User user;
    private Interest interest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        profile = new Profile();
        profile.setId(1L);
        profile.setName("Anna");
        profile.setAge(22);
        profile.setCity("Moscow");
        profile.setUser(user);

        profileDto = new ProfileDto();
        profileDto.setId(1L);
        profileDto.setName("Anna");
        profileDto.setAge(22);
        profileDto.setCity("Moscow");
        profileDto.setUserId(1L);

        interest = new Interest();
        interest.setId(1L);
        interest.setName("Hiking");
    }

    @Test
    void testGetProfileById_whenCached_shouldReturnFromCache() {
        when(cacheService.getFromCache("profile_1", ProfileDto.class)).thenReturn(profileDto);

        ProfileDto result = profileService.getProfileById(1L);

        assertEquals("Anna", result.getName());
        verify(profileRepository, never()).findById(any());
    }

    @Test
    void testGetProfileById_whenNotCached_shouldReturnFromDB() {
        when(cacheService.getFromCache("profile_1", ProfileDto.class)).thenReturn(null);
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileMapper.toDto(profile)).thenReturn(profileDto);

        ProfileDto result = profileService.getProfileById(1L);

        assertEquals("Anna", result.getName());
        verify(cacheService).saveToCache("profile_1", profileDto);
    }

    @Test
    void testGetProfileById_whenNotFound_shouldThrow() {
        when(cacheService.getFromCache("profile_99", ProfileDto.class)).thenReturn(null);
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> profileService.getProfileById(99L));
    }

    @Test
    void testCreateProfile_whenValid_shouldCreate() {
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(profileRepository.save(any())).thenReturn(profile);
        when(profileMapper.toDto(profile)).thenReturn(profileDto);

        ProfileDto created = profileService.createProfile(profileDto);

        assertEquals("Anna", created.getName());
        verify(profileRepository).save(any());
        verify(cacheService).clearCache("all_profiles");
    }

    @Test
    void testCreateProfile_whenAlreadyExists_shouldThrow() {
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        assertThrows(IllegalArgumentException.class, () -> profileService.createProfile(profileDto));
    }

    @Test
    void testUpdateProfile_whenValid_shouldUpdate() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any())).thenReturn(profile);
        when(profileMapper.toDto(profile)).thenReturn(profileDto);

        ProfileDto updated = profileService.updateProfile(1L, profileDto);

        assertEquals("Anna", updated.getName());
        verify(cacheService).clearCache("profile_1");
    }

    @Test
    void testUpdateProfile_whenNotFound_shouldThrow() {
        when(profileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> profileService.updateProfile(99L, profileDto));
    }

    @Test
    void testDeleteProfile_whenExists_shouldDelete() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        profileService.deleteProfile(1L);

        verify(profileRepository).delete(profile);
        verify(cacheService).clearCache("all_profiles");
        verify(cacheService).clearCache("profile_1");
    }

    @Test
    void testDeleteProfile_whenNotFound_shouldThrow() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> profileService.deleteProfile(1L));
    }

    @Test
    void testAddInterestToProfile_whenValid_shouldAdd() {
        profile.setInterests(new ArrayList<>());

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));
        when(profileRepository.save(profile)).thenReturn(profile);
        when(profileMapper.toDto(profile)).thenReturn(profileDto);

        ProfileDto result = profileService.addInterestToProfile(1L, 1L);

        assertEquals("Anna", result.getName());
        verify(cacheService).clearCache("all_profiles");
    }

    @Test
    void testAddInterestToProfile_whenAlreadyPresent_shouldNotAddAgain() {
        profile.setInterests(new ArrayList<>(List.of(interest)));

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));
        when(profileMapper.toDto(profile)).thenReturn(profileDto);

        ProfileDto result = profileService.addInterestToProfile(1L, 1L);

        verify(profileRepository, never()).save(any());
        assertEquals("Anna", result.getName());
    }
}
