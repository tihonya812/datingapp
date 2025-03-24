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

    public PhotoService(PhotoRepository photoRepository, ProfileRepository profileRepository,
                        PhotoMapper photoMapper) {
        this.photoRepository = photoRepository;
        this.profileRepository = profileRepository;
        this.photoMapper = photoMapper;
    }

    public PhotoDto addPhoto(Long profileId, String url) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Профиль с ID " + profileId + " не найден"));

        Photo photo = new Photo();
        photo.setUrl(url);
        photo.setProfile(profile);

        Photo savedPhoto = photoRepository.save(photo);
        return photoMapper.toDto(savedPhoto);
    }

    public List<PhotoDto> getPhotosByProfile(Long profileId) {
        if (!profileRepository.existsById(profileId)) {
            throw new NotFoundException("Профиль с ID " + profileId + " не найден");
        }

        List<Photo> photos = photoRepository.findByProfileId(profileId);
        return photos.stream().map(photoMapper::toDto).toList();
    }

    public void deletePhoto(Long photoId) {
        if (!photoRepository.existsById(photoId)) {
            throw new NotFoundException("Фото с ID " + photoId + " не найдено");
        }
        photoRepository.deleteById(photoId);
    }
}
