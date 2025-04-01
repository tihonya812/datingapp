package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.PhotoMapper;
import com.tihonya.datingapp.model.Photo;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.PhotoRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final ProfileRepository profileRepository;
    private final PhotoMapper photoMapper;
    private final CacheService cacheService;

    public PhotoService(PhotoRepository photoRepository, ProfileRepository profileRepository,
                        PhotoMapper photoMapper, CacheService cacheService) {
        this.photoRepository = photoRepository;
        this.profileRepository = profileRepository;
        this.photoMapper = photoMapper;
        this.cacheService = cacheService;
    }

    public PhotoDto addPhoto(Long profileId, String url) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Профиль с ID " + profileId + " не найден"));

        Photo photo = new Photo();
        photo.setUrl(url);
        photo.setProfile(profile);

        Photo savedPhoto = photoRepository.save(photo);
        clearPhotoCache(profileId);
        return photoMapper.toDto(savedPhoto);
    }

    public List<PhotoDto> getPhotosByProfile(Long profileId) {
        //        if (!profileRepository.existsById(profileId)) {
        //            throw new NotFoundException("Профиль с ID " + profileId + " не найден");
        //        }
        //
        //        List<Photo> photos = photoRepository.findByProfileId(profileId);
        //        return photos.stream().map(photoMapper::toDto).toList();
        String cacheKey = "photos_profile_" + profileId;
        List<PhotoDto> cachedPhotos = cacheService.getFromCache(cacheKey, List.class);
        if (cachedPhotos != null) {
            return cachedPhotos;
        }

        if (!profileRepository.existsById(profileId)) {
            throw new NotFoundException("Profile not found");
        }

        List<Photo> photos = photoRepository.findByProfileId(profileId);
        List<PhotoDto> photoDtos = photos.stream().map(photoMapper::toDto).toList();
        cacheService.saveToCache(cacheKey, photoDtos);
        return photoDtos;
    }

    public void clearPhotoCache(Long profileId) {
        cacheService.clearCache("photos_profile_" + profileId);
    }

    public void deletePhoto(Long photoId) {
        //        if (!photoRepository.existsById(photoId)) {
        //            throw new NotFoundException("Фото с ID " + photoId + " не найдено");
        //        }
        //        photoRepository.deleteById(photoId);

        Photo photo = photoRepository.findById(photoId)
                    .orElseThrow(() -> new NotFoundException("Фото с ID " + photoId + " не найдено"));
        Long profileId = photo.getProfile().getId();

        photoRepository.deleteById(photoId);

        clearPhotoCache(profileId);
    }
}
