package com.tihonya.datingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterestDto {
    private Long id;
    @NotBlank (message = "Название интереса не может быть пустым")
    private String name;
}