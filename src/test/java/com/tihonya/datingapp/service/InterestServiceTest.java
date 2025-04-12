package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.InterestMapper;
import com.tihonya.datingapp.model.Interest;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.InterestRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InterestServiceTest {

    @Mock
    private InterestRepository interestRepository;

    @Mock
    private InterestMapper interestMapper;

    @Mock
    private CacheService cacheService;

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private InterestService interestService;

    private Interest interest;
    private InterestDto interestDto;

    @BeforeEach
    void setUp() {
        interest = new Interest();
        interest.setId(1L);
        interest.setName("Music");

        interestDto = new InterestDto();
        interestDto.setId(1L);
        interestDto.setName("Music");
    }

    @Test
    void testCreateInterest_whenValid_shouldSaveAndReturnDto() {
        when(interestRepository.save(any(Interest.class))).thenReturn(interest);
        when(interestMapper.toDto(any(Interest.class))).thenReturn(interestDto);

        InterestDto result = interestService.createInterest(interestDto);

        assertEquals("Music", result.getName());
        verify(interestRepository).save(any(Interest.class));
        verify(cacheService).clearCache("all_interests");
    }

    @Test
    void testGetAllInterests_shouldReturnAllInterests() {
        List<Interest> interests = List.of(interest);
        when(interestRepository.findAll()).thenReturn(interests);
        when(interestMapper.toDto(interest)).thenReturn(interestDto);

        List<InterestDto> result = interestService.getAllInterests();

        assertEquals(1, result.size());
        assertEquals("Music", result.getFirst().getName());
    }

    @Test
    void testGetInterestById_whenCached_shouldReturnFromCache() {
        when(cacheService.getFromCache("interest_1", InterestDto.class)).thenReturn(interestDto);

        InterestDto result = interestService.getInterestById(1L);

        assertEquals("Music", result.getName());
        verify(interestRepository, never()).findById(anyLong());
    }

    @Test
    void testGetInterestById_whenNotCached_shouldReturnFromDbAndCacheIt() {
        when(cacheService.getFromCache("interest_1", InterestDto.class)).thenReturn(null);
        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));
        when(interestMapper.toDto(interest)).thenReturn(interestDto);

        InterestDto result = interestService.getInterestById(1L);

        assertEquals("Music", result.getName());
        verify(cacheService).saveToCache("interest_1", interestDto);
    }

    @Test
    void testGetInterestById_whenNotFound_shouldThrow() {
        when(cacheService.getFromCache("interest_99", InterestDto.class)).thenReturn(null);
        when(interestRepository.findById(99L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> interestService.getInterestById(99L));
        assertEquals("Interest not found", ex.getMessage());
    }

    @Test
    void testUpdateInterest_whenValid_shouldUpdateAndReturnDto() {
        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));
        when(interestRepository.save(interest)).thenReturn(interest);
        when(interestMapper.toDto(interest)).thenReturn(interestDto);

        InterestDto updatedInterest = interestService.updateInterest(1L, interestDto);

        assertEquals("Music", updatedInterest.getName());
        verify(interestRepository).save(interest);
        verify(cacheService).clearCache("all_interests");
    }

    @Test
    void testUpdateInterest_whenNotFound_shouldThrow() {
        when(interestRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> interestService.updateInterest(1L, interestDto));
        assertEquals("Interest not found", ex.getMessage());
    }

    @Test
    void testDeleteInterest_whenValid_shouldDeleteAndClearCache() {
        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));

        interestService.deleteInterest(1L);

        verify(interestRepository).delete(interest);
        verify(cacheService).clearCache("all_interests");
    }

    @Test
    void testDeleteInterest_whenNotFound_shouldThrow() {
        when(interestRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> interestService.deleteInterest(1L));
        assertEquals("Interest not found", ex.getMessage());
    }

    @Test
    void testDeleteInterest_shouldRemoveInterestFromProfiles() {
        Profile profile = new Profile();
        profile.setId(1L);
        profile.setInterests(new ArrayList<>(List.of(interest)));
        interest.setProfiles(new ArrayList<>(List.of(profile)));

        when(interestRepository.findById(1L)).thenReturn(Optional.of(interest));
        when(profileRepository.saveAll(anyList())).thenReturn(List.of(profile));

        interestService.deleteInterest(1L);

        assertTrue(profile.getInterests().isEmpty());
        verify(profileRepository).saveAll(anyList());
    }

    @Test
    void testCreateInterests_whenBulk_shouldSaveAndReturnDto() {
        List<InterestDto> interestDtos = List.of(interestDto);
        when(interestMapper.toDto(any(Interest.class))).thenReturn(interestDto);
        when(interestRepository.saveAll(anyList())).thenReturn(List.of(interest));

        List<InterestDto> result = interestService.createInterests(interestDtos);

        assertEquals(1, result.size());
        assertEquals("Music", result.getFirst().getName());
        verify(interestRepository).saveAll(anyList());
    }
}

