package com.tihonya.datingapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class ProfileDto {
    private Long id;
    private String bio;
    private Long userId;
    private List<PreferenceDto> preferences;
}

