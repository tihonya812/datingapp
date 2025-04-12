package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.model.Like;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.LikeRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
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
class MatchServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private MatchService matchService;

    private Profile profile1;
    private Profile profile2;
    private Like like;
    private ProfileDto profileDto1;
    private ProfileDto profileDto2;

    @BeforeEach
    void setUp() {
        profile1 = new Profile();
        profile1.setId(1L);
        profile1.setAge(25);

        profile2 = new Profile();
        profile2.setId(2L);
        profile2.setAge(23);

        like = new Like();
        like.setId(1L);
        like.setLiker(profile1);
        like.setLiked(profile2);

        profileDto1 = new ProfileDto();
        profileDto1.setId(1L);
        profileDto1.setAge(25);

        profileDto2 = new ProfileDto();
        profileDto2.setId(2L);
        profileDto2.setAge(23);
    }

    @Test
    void testGetMatches_whenMatchesExist_shouldReturnMatchesFromCache() {
        List<ProfileDto> cachedMatches = List.of(profileDto1, profileDto2);
        String cacheKey = "matches_1_20_30";
        when(cacheService.getFromCache(cacheKey, List.class)).thenReturn(cachedMatches);

        List<ProfileDto> result = matchService.getMatches(1L, 20, 30);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(profileRepository, never()).findById(anyLong());
        verify(likeRepository, never()).findMatchesByAge(any(), anyInt(), anyInt());
    }

    @Test
    void testGetMatches_whenProfileNotFound_shouldThrowNotFoundException() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> matchService.getMatches(1L, 20, 30));
        assertEquals("Profile not found", exception.getMessage());
    }

    @Test
    void testLikeProfile_whenProfilesAreValid_shouldSaveLike() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile1));
        when(profileRepository.findById(2L)).thenReturn(Optional.of(profile2));
        when(likeRepository.findLike(profile1, profile2)).thenReturn(Optional.empty());

        matchService.likeProfile(1L, 2L);

        verify(likeRepository).save(any(Like.class));
        verify(cacheService).clearMatchesCache(1L);
        verify(cacheService).clearMatchesCache(2L);
    }

    @Test
    void testLikeProfile_whenLikerAndLikedAreSame_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.likeProfile(1L, 1L));
        assertEquals("Нельзя лайкать свой профиль!", exception.getMessage());
    }

    @Test
    void testLikeProfile_whenLikeAlreadyExists_shouldThrowIllegalArgumentException() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile1));
        when(profileRepository.findById(2L)).thenReturn(Optional.of(profile2));
        when(likeRepository.findLike(profile1, profile2)).thenReturn(Optional.of(like));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> matchService.likeProfile(1L, 2L));
        assertEquals("Лайк уже был поставлен!", exception.getMessage());
    }

    @Test
    void testLikeProfile_whenProfileNotFound_shouldThrowNotFoundException() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> matchService.likeProfile(1L, 2L));
        assertEquals("Profile not found", exception.getMessage());
    }
}
