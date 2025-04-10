package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.service.MatchService;
import com.tihonya.datingapp.service.ProfileService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final MatchService matchService;

    @GetMapping
    public List<ProfileDto> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    @GetMapping("/{id}")
    public ProfileDto getProfileById(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }

    @PostMapping
    public ProfileDto createProfile(@Valid @RequestBody ProfileDto profileDto) {
        return profileService.createProfile(profileDto);
    }

    @PutMapping("/{id}")
    public ProfileDto updateProfile(@PathVariable Long id, @Valid @RequestBody ProfileDto profileDto) {
        return profileService.updateProfile(id, profileDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
    }

    @GetMapping("/{profileId}/matches")
    public ResponseEntity<List<ProfileDto>> getMatches(
            @PathVariable Long profileId,
            @RequestParam(required = false, defaultValue = "18") int minAge,
            @RequestParam(required = false, defaultValue = "99") int maxAge
    ) {
        return ResponseEntity.ok(matchService.getMatches(profileId, minAge, maxAge));
    }

    @PostMapping("/{likerId}/like/{likedId}")
    public ResponseEntity<String> likeProfile(@PathVariable Long likerId, @PathVariable Long likedId) {
        matchService.likeProfile(likerId, likedId);
        return ResponseEntity.ok("Лайк поставлен!");
    }
}