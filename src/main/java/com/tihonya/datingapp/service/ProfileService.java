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
    private static final String CACHE_KEY_PROFILES = "all_profiles";

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final CacheService cacheService;

    @Transactional
    public List<ProfileDto> getAllProfiles() {
        List<ProfileDto> cahedProfiles = cacheService.getFromCache(CACHE_KEY_PROFILES, List.class);
        if (cahedProfiles != null) {
            return cahedProfiles;
        }

        List<ProfileDto> profiles = profileMapper.toDtoList(profileRepository.findAll());

        cacheService.saveToCache(CACHE_KEY_PROFILES, profiles);

        return profiles;
    }

    public void clearProfileCache() {
        cacheService.clearCache(CACHE_KEY_PROFILES);
    }

    public void clearProfileCache(Long id) {
        cacheService.clearCache("profile_" + id);
    }

    @Transactional
    public ProfileDto getProfileById(Long id) {
        //        Profile profile = profileRepository.findById(id)
        //                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
        //        return profileMapper.toDto(profile);
        String cacheKey = "profile_" + id;

        // Проверяем, есть ли профиль в кэше
        ProfileDto cachedProfile = cacheService.getFromCache(cacheKey, ProfileDto.class);
        if (cachedProfile != null) {
            return cachedProfile;
        }

        // Если нет, загружаем из БД
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        ProfileDto profileDto = profileMapper.toDto(profile);

        // Сохраняем в кэш
        cacheService.saveToCache(cacheKey, profileDto);

        return profileDto;
    }

    @Transactional
    public ProfileDto createProfile(ProfileDto profileDto) {
        Profile profile = new Profile();
        profile.setName(profileDto.getName());
        profile.setAge(profileDto.getAge());
        profile.setCity(profileDto.getCity());
        profile.setBio(profileDto.getBio());

        User user = userRepository.findById(profileDto.getUserId())
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        profile.setUser(user);

        clearProfileCache();
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Transactional
    public ProfileDto updateProfile(Long id, ProfileDto profileDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        profile.setName(profileDto.getName());
        profile.setAge(profileDto.getAge());
        profile.setCity(profileDto.getCity());
        profile.setBio(profileDto.getBio());

        clearProfileCache(id);
        return profileMapper.toDto(profileRepository.save(profile));
    }

    @Transactional
    public void deleteProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
        profileRepository.delete(profile);
        clearProfileCache();    // Очистка всего кэша профилей (так как один удалён)
        clearProfileCache(id);  // Очистка конкретного кэша профиля (перестраховка)
    }
    /*
        @Transactional
    public List<ProfileDto> getMatches(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
        List<Profile> matches = profileRepository.findMatches(profile);
        return profileMapper.toDtoList(matches);
    }

    @Transactional
    public void likeProfile(Long likerId, Long likedId) {
        if (likerId.equals(likedId)) {
            throw new IllegalArgumentException("Нельзя лайкать свой профиль!");
        }

        Profile liker = profileRepository.findById(likerId)
                .orElseThrow(() -> new NotFoundException("Профиль не найден"));

        Profile liked = profileRepository.findById(likedId)
                .orElseThrow(() -> new NotFoundException("Профиль не найден"));

        if (!liker.getLikedProfiles().contains(liked)) {
            liker.getLikedProfiles().add(liked);
            profileRepository.save(liker);
        } else {
            throw new IllegalArgumentException("Лайк уже был поставлен!");
        }
    }
     */
}

