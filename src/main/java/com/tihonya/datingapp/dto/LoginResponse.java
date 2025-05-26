package com.tihonya.datingapp.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
    private UserDto user;
    private Long profileId; // Добавляем поле

    public LoginResponse(String token, String role, UserDto user, Long profileId) {
        this.token = token;
        this.role = role;
        this.user = user;
        this.profileId = profileId;
    }
}