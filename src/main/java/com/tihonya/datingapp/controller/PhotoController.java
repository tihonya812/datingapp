package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/photos")
@Tag(name = "photos", description = "API для работы с фотками")
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @Operation(summary = "Добавить фото профилю", description = "Добавляет фото для указанного профиля")
    @PostMapping("/{profileId}")
    public ResponseEntity<PhotoDto> addPhoto(
            @Parameter(description = "Идентификатор профиля") @PathVariable Long profileId,
            @Parameter(description = "Файл фото") @RequestParam("file") MultipartFile file) {
        PhotoDto photo = photoService.addPhoto(profileId, file);
        return ResponseEntity.ok(photo);
    }

    @Operation(summary = "Получить фото профиля",
            description = "Возвращает список фото для указанного профиля")
    @GetMapping("/{profileId}")
    public ResponseEntity<List<PhotoDto>> getPhotosByProfile(
            @Parameter(description = "Идентификатор профиля")
            @PathVariable Long profileId) {
        List<PhotoDto> photos = photoService.getPhotosByProfile(profileId);
        return ResponseEntity.ok(photos);
    }

    @Operation(summary = "Удалить фото", description = "Удаляет фото по ID")
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "Идентификатор фотки")
            @PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
