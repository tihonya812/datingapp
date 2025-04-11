package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.service.PreferenceService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
@Tag(name = "Preferences", description = "API для работы с предпочтениями пользователей")
public class PreferenceController {
    private final PreferenceService preferenceService;

    @Operation(summary = "Создать новое предпочтение",
            description = "Создает новое предпочтение пользователя")
    @PostMapping
    public ResponseEntity<PreferenceDto> createPreference(
            @Parameter(description = "Данные предпочтения")
            @Valid @RequestBody PreferenceDto preferenceDto) {
        return ResponseEntity.ok(preferenceService.createPreference(preferenceDto));
    }

    @Operation(summary = "Получить все предпочтения", description = "Возвращает список всех предпочтений")
    @GetMapping
    public ResponseEntity<List<PreferenceDto>> getAllPreferences() {
        return ResponseEntity.ok(preferenceService.getAllPreferences());
    }

    @Operation(summary = "Получить предпочтение по ID", description = "Возвращает предпочтение по ID")
    @GetMapping("/{id}")
    public ResponseEntity<PreferenceDto> getPreferenceById(
            @Parameter(description = "Идентификатор предпочтения")
            @PathVariable Long id) {
        return ResponseEntity.ok(preferenceService.getPreferenceById(id));
    }

    @Operation(summary = "Обновить предпочтение", description = "Обновляет предпочтение пользователя по ID")
    @PutMapping("/{id}")
    public ResponseEntity<PreferenceDto> updatePreference(
            @Parameter(description = "Идентификатор предпочтения") @PathVariable Long id,
            @Parameter(description = "Данные для обновления предпочтения")
            @Valid @RequestBody PreferenceDto preferenceDto) {
        return ResponseEntity.ok(preferenceService.updatePreference(id, preferenceDto));
    }

    @Operation(summary = "Удалить предпочтение", description = "Удаляет предпочтение по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(
            @Parameter(description = "Идентификатор предпочтения")
            @PathVariable Long id) {
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }
}
