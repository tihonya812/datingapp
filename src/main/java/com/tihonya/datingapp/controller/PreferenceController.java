package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.PreferenceDto;
import com.tihonya.datingapp.service.PreferenceService;
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
public class PreferenceController {
    private final PreferenceService preferenceService;

    @PostMapping
    public ResponseEntity<PreferenceDto> createPreference(@Valid @RequestBody PreferenceDto preferenceDto) {
        return ResponseEntity.ok(preferenceService.createPreference(preferenceDto));
    }

    @GetMapping
    public ResponseEntity<List<PreferenceDto>> getAllPreferences() {
        return ResponseEntity.ok(preferenceService.getAllPreferences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PreferenceDto> getPreferenceById(@PathVariable Long id) {
        return ResponseEntity.ok(preferenceService.getPreferenceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PreferenceDto> updatePreference(@PathVariable Long id,
                                                          @Valid @RequestBody PreferenceDto preferenceDto) {
        return ResponseEntity.ok(preferenceService.updatePreference(id, preferenceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePreference(@PathVariable Long id) {
        preferenceService.deletePreference(id);
        return ResponseEntity.noContent().build();
    }
}
