package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.ProfileDto;
import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.service.ProfileService;
import com.tihonya.datingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "API для админских функций")
public class AdminController {
    private final UserService userService;
    private final ProfileService profileService;

    @Operation(summary = "Админская панель", description = "Возвращает приветствие для админа")
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminDashboard() {
        return "Добро пожаловать в админ-панель!";
    }

    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей для админа")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя по ID")
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Идентификатор пользователя")
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Пользователь успешно удалён");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }

    @Operation(summary = "Получить все профили", description = "Возвращает список всех профилей для админа")
    @GetMapping("/profiles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProfileDto>> getAllProfiles() {
        return ResponseEntity.ok(profileService.getAllProfiles());
    }

    @Operation(summary = "Удалить профиль", description = "Удаляет профиль по ID")
    @DeleteMapping("/profiles/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteProfile(
            @Parameter(description = "Идентификатор профиля")
            @PathVariable Long id) {
        try {
            profileService.deleteProfile(id);
            return ResponseEntity.ok("Профиль успешно удалён");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Ошибка при удалении профиля: " + e.getMessage());
        }
    }
}
