package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.UserDto;
import com.tihonya.datingapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API для работы с пользователями")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей")
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному ID")
    @GetMapping("/{id}")
    public UserDto getUserById(
            @Parameter(description = "Идентификатор пользователя")
            @PathVariable Long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Создание нового пользователя",
            description = "Создает нового пользователя в системе")
    @PostMapping
    public UserDto createUser(
            @Parameter(description = "Данные пользователя")
            @Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Обновление пользователя",
            description = "Обновляет пользователя по указанному ID")
    @PutMapping("/{id}")
    public UserDto updateUser(
            @Parameter(description = "Идентификатор пользователя")
            @PathVariable Long id,
            @Parameter(description = "Данные пользователя для обновления")
            @Valid @RequestBody UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Удаление пользователя",
            description = "Удаляет пользователя по указанному ID")
    @DeleteMapping("/{id}")
    public void deleteUser(
            @Parameter(description = "Идентификатор пользователя")
            @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Добавить интерес пользователю",
            description = "Добавляет интерес пользователю по указанным ID")
    @PostMapping("/{userId}/interests/{interestId}")
    public UserDto addInterestToUser(
            @Parameter(description = "Идентификатор пользователя")
            @PathVariable Long userId,
            @Parameter(description = "Идентификатор интереса")
            @PathVariable Long interestId) {
        return userService.addInterestToUser(userId, interestId);
    }
}

