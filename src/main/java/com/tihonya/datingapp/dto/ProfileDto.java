package com.tihonya.datingapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class ProfileDto {
    private Long id;
    private String name;
    private int age;
    private String city;
    private String bio;
    private Long userId;
    private List<PhotoDto> photos;
    private List<PreferenceDto> preferences;
}

