package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.ProfileMapper;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.ProfileRepository;
import com.tihonya.datingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final String USER_NOT_FOUND = "User not found";
    private static final String PROFILE_NOT_FOUND = "Profile not found";

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;

    @Transactional
    public List<ProfileDto> getAllProfiles() {
        return profileMapper.toDtoList(profileRepository.findAll());
    }

    @Transactional
    public ProfileDto getProfileById(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
        return profileMapper.toDto(profile);
    }

    @Transactional
    public ProfileDto createProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setBio(profileDto.getBio());

        User user = userRepository.findById(profileDto.getUserId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        profile.setUser(user);

        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Transactional
    public ProfileDto updateProfile(Long id, ProfileDto profileDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        profile.setBio(profileDto.getBio());

        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Transactional
    public void deleteProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        profileRepository.delete(profile);
    }
}

