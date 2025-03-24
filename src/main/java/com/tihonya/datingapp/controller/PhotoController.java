package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.service.PhotoService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    @PostMapping("/{profileId}")
    public ResponseEntity<PhotoDto> addPhoto(@PathVariable Long profileId, @RequestParam String url) {
        PhotoDto photo = photoService.addPhoto(profileId, url);
        return ResponseEntity.ok(photo);
    }

    @GetMapping("/{profileId}")
    public ResponseEntity<List<PhotoDto>> getPhotosByProfile(@PathVariable Long profileId) {
        List<PhotoDto> photos = photoService.getPhotosByProfile(profileId);
        return ResponseEntity.ok(photos);
    }

    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }
}
