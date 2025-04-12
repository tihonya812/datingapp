package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.service.MatchService;
import com.tihonya.datingapp.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Profiles", description = "API для работы с профилями пользователей")
public class ProfileController {
    private final ProfileService profileService;
    private final MatchService matchService;

    @Operation(summary = "Получить все профили",
            description = "Возвращает список всех профилей")
    @GetMapping
    public List<ProfileDto> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    @Operation(summary = "Получить профиль по ID",
            description = "Возвращает профиль по его идентификатору")
    @GetMapping("/{id}")
    public ProfileDto getProfileById(
            @Parameter(description = "Идентификатор профиля")
            @PathVariable Long id) {
        return profileService.getProfileById(id);
    }

    @Operation(summary = "Создать новый профиль", description = "Создает новый профиль пользователя")
    @PostMapping
    public ProfileDto createProfile(
            @Parameter(description = "Данные профиля")
            @Valid @RequestBody ProfileDto profileDto) {
        return profileService.createProfile(profileDto);
    }

    @Operation(summary = "Обновить профиль", description = "Обновляет данные профиля по ID")
    @PutMapping("/{id}")
    public ProfileDto updateProfile(
            @Parameter(description = "Идентификатор профиля")
            @PathVariable Long id,
            @Parameter(description = "Данные для обновления профиля")
            @Valid @RequestBody ProfileDto profileDto) {
        return profileService.updateProfile(id, profileDto);
    }

    @Operation(summary = "Удалить профиль", description = "Удаляет профиль по ID")
    @DeleteMapping("/{id}")
    public void deleteProfile(
            @Parameter(description = "Идентификатор профиля")
            @PathVariable Long id) {
        profileService.deleteProfile(id);
    }

    @Operation(summary = "Получить совпадения профиля",
            description = "Возвращает список совпадений для профиля в заданном диапазоне возраста")
    @GetMapping("/{profileId}/matches")
    public ResponseEntity<List<ProfileDto>> getMatches(
            @Parameter(description = "Идентификатор профиля") @PathVariable Long profileId,
            @Parameter(description = "Минимальный возраст для совпадений", example = "18")
            @RequestParam(required = false, defaultValue = "18") int minAge,
            @Parameter(description = "Максимальный возраст для совпадений", example = "99")
            @RequestParam(required = false, defaultValue = "99") int maxAge
    ) {
        return ResponseEntity.ok(matchService.getMatches(profileId, minAge, maxAge));
    }

    @Operation(summary = "Лайк профиля", description = "Поставить лайк другому профилю")
    @PostMapping("/{likerId}/like/{likedId}")
    public ResponseEntity<String> likeProfile(
            @Parameter(description = "Идентификатор профиля, ставящего лайк") @PathVariable Long likerId,
            @Parameter(description = "Идентификатор профиля, получающего лайк") @PathVariable Long likedId) {
        matchService.likeProfile(likerId, likedId);
        return ResponseEntity.ok("Лайк поставлен!");
    }

    @Operation(summary = "Добавить Интерес профилю",
            description = "Добавляет интерес профилю по указанному ID")
    @PostMapping("/{profileId}/interests/{interestsId}")
    public ProfileDto addInterestToProfile(
            @Parameter (description = "Идентификатор профиля")
            @PathVariable Long profileId,
            @Parameter (description = "Идентификатор интереса")
            @PathVariable Long interestsId) {
        return profileService.addInterestToProfile(profileId, interestsId);
    }
}