package com.tihonya.datingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PreferenceDto {
    private Long id;

    @NotBlank(message = "Категория не может быть пустой")
    private String category;

    @NotBlank(message = "Значение не может быть пустым")
    private String value;
    private Long profileId;
}

