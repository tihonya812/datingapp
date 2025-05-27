package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.exception.PhotoDeletionException;
import com.tihonya.datingapp.exception.PhotoStorageException;
import com.tihonya.datingapp.mapper.PhotoMapper;
import com.tihonya.datingapp.model.Photo;
import com.tihonya.datingapp.model.Profile;
import com.tihonya.datingapp.repository.PhotoRepository;
import com.tihonya.datingapp.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public PhotoDto addPhoto(Long profileId, MultipartFile file) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Профиль с ID " + profileId + " не найден"));

        // Транслитерация имени файла (удаляем русские символы)
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + "_" + originalFileName
                .replaceAll("[^a-zA-Z0-9.-]", "_"); // Заменяем все не-ASCII символы на "_"
        Path uploadPath = Paths.get("uploads/");

        // Сохранение файла
        try {
            Files.createDirectories(uploadPath); // Создаём папку, если её нет
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PhotoStorageException("Ошибка при сохранении файла: ", e);
        }

        // Генерация URL
        String url = "http://localhost:8080/uploads/" + fileName;

        Photo photo = new Photo();
        photo.setUrl(url);
        photo.setProfile(profile);

        Photo savedPhoto = photoRepository.save(photo);
        clearPhotoCache(profileId); // Очистка кэша для профиля
        return photoMapper.toDto(savedPhoto);
    }

    public List<PhotoDto> getPhotosByProfile(Long profileId) {
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
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new NotFoundException("Фото с ID " + photoId + " не найдено"));
        Long profileId = photo.getProfile().getId();

        // Удаление файла с диска
        String url = photo.getUrl();
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        Path path = Paths.get("uploads/" + fileName);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new PhotoDeletionException("Ошибка при удалении файла: ", e);
        }

        photoRepository.delete(photo);
        clearPhotoCache(profileId);
    }
}
