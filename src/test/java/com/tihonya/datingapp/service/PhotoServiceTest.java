package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.PhotoMapper;
import com.tihonya.datingapp.model.Photo;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.PhotoRepository;
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
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private PhotoMapper photoMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private PhotoService photoService;

    private Photo photo;
    private PhotoDto photoDto;
    private Profile profile;

    @BeforeEach
    void setUp() {
        profile = new Profile();
        profile.setId(1L);

        photo = new Photo();
        photo.setId(1L);
        photo.setUrl("http://example.com/photo.jpg");
        photo.setProfile(profile);

        photoDto = new PhotoDto();
        photoDto.setId(1L);
        photoDto.setUrl("http://example.com/photo.jpg");
    }

    @Test
    void testAddPhoto_whenProfileExists_shouldAddAndReturnPhotoDto() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));
        when(photoRepository.save(any(Photo.class))).thenReturn(photo);
        when(photoMapper.toDto(photo)).thenReturn(photoDto);

        PhotoDto result = photoService.addPhoto(1L, "http://example.com/photo.jpg");

        assertEquals("http://example.com/photo.jpg", result.getUrl());
        verify(photoRepository).save(any(Photo.class));
        verify(cacheService).clearCache("photos_profile_1");
    }

    @Test
    void testAddPhoto_whenProfileNotFound_shouldThrowNotFoundException() {
        when(profileRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> photoService.addPhoto(1L, "http://example.com/photo.jpg"));
        assertEquals("Профиль с ID 1 не найден", exception.getMessage());
    }

    @Test
    void testGetPhotosByProfile_whenCached_shouldReturnFromCache() {
        List<PhotoDto> cachedPhotos = List.of(photoDto);
        when(cacheService.getFromCache("photos_profile_1", List.class)).thenReturn(cachedPhotos);

        List<PhotoDto> result = photoService.getPhotosByProfile(1L);

        assertEquals(1, result.size());
        assertEquals("http://example.com/photo.jpg", result.getFirst().getUrl());
        verify(profileRepository, never()).existsById(anyLong());
        verify(photoRepository, never()).findByProfileId(anyLong());
    }

    @Test
    void testGetPhotosByProfile_whenNotCached_shouldReturnFromDbAndCacheIt() {
        List<Photo> photos = List.of(photo);
        List<PhotoDto> photoDtos = List.of(photoDto);

        when(cacheService.getFromCache("photos_profile_1", List.class)).thenReturn(null);
        when(profileRepository.existsById(1L)).thenReturn(true);
        when(photoRepository.findByProfileId(1L)).thenReturn(photos);
        when(photoMapper.toDto(photo)).thenReturn(photoDto);

        List<PhotoDto> result = photoService.getPhotosByProfile(1L);

        assertEquals(1, result.size());
        assertEquals("http://example.com/photo.jpg", result.getFirst().getUrl());
        verify(cacheService).saveToCache("photos_profile_1", photoDtos);
    }

    @Test
    void testGetPhotosByProfile_whenProfileNotFound_shouldThrowNotFoundException() {
        when(profileRepository.existsById(1L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> photoService.getPhotosByProfile(1L));
        assertEquals("Profile not found", exception.getMessage());
    }

    @Test
    void testDeletePhoto_whenPhotoExists_shouldDeleteAndClearCache() {
        when(photoRepository.findById(1L)).thenReturn(Optional.of(photo));

        photoService.deletePhoto(1L);

        verify(photoRepository).deleteById(1L);
        verify(cacheService).clearCache("photos_profile_1");
    }

    @Test
    void testDeletePhoto_whenPhotoNotFound_shouldThrowNotFoundException() {
        when(photoRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> photoService.deletePhoto(1L));
        assertEquals("Фото с ID 1 не найдено", exception.getMessage());
    }

    @Test
    void testClearPhotoCache_shouldClearCache() {
        photoService.clearPhotoCache(1L);

        verify(cacheService).clearCache("photos_profile_1");
    }
}

