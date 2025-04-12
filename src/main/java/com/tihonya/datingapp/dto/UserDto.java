package com.tihonya.datingapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    private Long id;

    @NotBlank (message = "Логин не может быть пустым")
    private String username;

    @Email (message = "Некорректный email")
    @NotBlank (message = "Email обязателен")
    private String email;

    @Size (min = 7, message = "Пароль должен быть не менее 7 символов")
    private String password;
    private String role;
    private ProfileDto profile;
}

