package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.ProfileMapper;
import com.tihonya.datingapp.model.Like;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.LikeRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final ProfileRepository profileRepository;
    private final LikeRepository likeRepository;
    private final ProfileMapper profileMapper;
    private final CacheService cacheService;
    private static final String PROFILE_NOT_FOUND = "Profile not found";
    private static final String MATCHES = "matches_";

    @Transactional
    public List<ProfileDto> getMatches(Long profileId, int minAge, int maxAge) {
        String cacheKey = MATCHES + profileId + "_" + minAge + "_" + maxAge;

        // Проверяем кэш
        List<ProfileDto> cachedMatches = cacheService.getFromCache(cacheKey, List.class);
        if (cachedMatches != null) {
            return cachedMatches;
        }

        // Запрос в БД
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        List<Profile> matches = likeRepository.findMatchesByAge(profile, minAge, maxAge);
        List<ProfileDto> matchDtos = matches.stream()
                .map(profileMapper::toDto)
                .toList();

        // Кладем в кэш
        cacheService.saveToCache(cacheKey, matchDtos);
        return matchDtos;
    }

    @Transactional
    public void likeProfile(Long likerId, Long likedId) {
        if (likerId.equals(likedId)) {
            throw new IllegalArgumentException("Нельзя лайкать свой профиль!");
        }

        Profile liker = profileRepository.findById(likerId)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));
        Profile liked = profileRepository.findById(likedId)
                .orElseThrow(() -> new NotFoundException(PROFILE_NOT_FOUND));

        // Проверяем, был ли лайк
        if (likeRepository.findLike(liker, liked).isPresent()) {
            throw new IllegalArgumentException("Лайк уже был поставлен!");
        }

        // Сохраняем новый лайк
        likeRepository.save(new Like(null, liker, liked, Instant.now()));

        // Чистим кэш
        cacheService.clearMatchesCache(likerId);
        cacheService.clearMatchesCache(likedId);
    }
}