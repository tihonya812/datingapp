package com.tihonya.datingapp.dto;

import lombok.Data;

@Data
public class PreferenceDto {
    private Long id;
    private String category;
    private String value;
    private Long profileId;
}

