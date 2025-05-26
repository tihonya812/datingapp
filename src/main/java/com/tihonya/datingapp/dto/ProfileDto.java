package com.tihonya.datingapp.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class ProfileDto {
    private Long id;

    @NotBlank (message = "Имя не может быть пустым")
    private String name;

    @Min(value = 18, message = "Минимальный возраст - 18 лет")
    @Max(value = 99, message = "Максимальный возраст - 99 лет")
    private int age;

    @NotBlank (message = "Город обязателен")
    private String city;

    @Size (max = 300, message = "Биография не должна превышать 300 символов")
    private String bio;
    private Long userId;
    private List<PhotoDto> photos;
    private List<PreferenceDto> preferences;
    private List<InterestDto> interests;
}

