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
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private static final String INTEREST_NOT_FOUND = "Interest not found";
    private static final String PROFILE_NOT_FOUND = "Profile not found";
    private static final String CACHE_KEY_PROFILES = "all_profiles";

    private final ProfileRepository profileRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;
    private final ProfileMapper profileMapper;
    private final CacheService cacheService;

    @Transactional
    public List<ProfileDto> getAllProfiles() {
        return profileMapper.toDtoList(profileRepository.findAll());
    }

    public void clearProfileCache() {
        cacheService.clearCache(CACHE_KEY_PROFILES);
    }

    public void clearProfileCache(Long id) {
        cacheService.clearCache("profile_" + id);
    }

    @Transactional
    public ProfileDto getProfileById(Long id) {
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
    public ProfileDto getProfileByUserId(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Profile not found for user ID: " + userId));
        return profileMapper.toDto(profile);
    }

    @Transactional
    public ProfileDto createProfile(ProfileDto profileDto) {
        if (profileDto.getUserId() != null) {
            Optional<Profile> existingProfile = profileRepository.findByUserId(profileDto.getUserId());
            if (existingProfile.isPresent()) {
                throw new IllegalArgumentException("Профиль для этого пользователя уже существует.");
            }
        }
        Profile profile = new Profile();
        profile.setName(profileDto.getName());
        profile.setAge(profileDto.getAge());
        profile.setCity(profileDto.getCity());
        profile.setBio(profileDto.getBio());

        if (profileDto.getUserId() != null) {
            User user = userRepository.findById(profileDto.getUserId())
                    .orElseThrow(() -> new NotFoundException("USER_NOT_FOUND"));
            profile.setUser(user);
            System.out.println("Привязан пользователь с ID: " + profileDto.getUserId());
        } else {
            profile.setUser(null);
        }

        clearProfileCache();
        Profile savedProfile = profileRepository.save(profile);
        ProfileDto result = profileMapper.toDto(savedProfile);
        System.out.println("Возвращаемый ProfileDto: " + result);
        return result;
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
        // Разрываем связь с пользователем
        User user = profile.getUser();
        if (user != null) {
            user.setProfile(null);
            userRepository.save(user);
        }
        profile.setUser(null);
        profileRepository.delete(profile);
        clearProfileCache();    // Очистка всего кэша профилей (так как один удалён)
        clearProfileCache(id);  // Очистка конкретного кэша профиля (перестраховка)
    }

    @Transactional
    public ProfileDto addInterestToProfile(Long profileId, Long interestId) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(()
                -> new NotFoundException(PROFILE_NOT_FOUND));
        Interest interest = interestRepository.findById(interestId).orElseThrow(()
                -> new NotFoundException(INTEREST_NOT_FOUND));

        // Проверяем, что интерес ещё не добавлен в профиль
        if (!profile.getInterests().contains(interest)) {
            profile.getInterests().add(interest);
            profileRepository.save(profile);
        }

        clearProfileCache();
        return profileMapper.toDto(profile);
    }

    @Transactional
    public Long getProfileIdByUserId(Long userId) {
        Logger logger = LoggerFactory.getLogger(ProfileService.class);
        Profile profile = profileRepository.findByUserId(userId)
                .orElse(null); // Возвращаем null, если профиль не найден
        Long profileId = profile != null ? profile.getId() : null;
        logger.info("Fetching profileId for userId: {}, result: {}", userId, profileId);
        return profile != null ? profile.getId() : null;
    }
}


