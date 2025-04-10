package com.tihonya.datingapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class InterestDto {
    @NotBlank (message = "Название интереса не может быть пустым")
    private Long id;
    private String name;
}