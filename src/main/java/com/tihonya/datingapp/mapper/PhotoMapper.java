package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.PhotoDto;
import com.tihonya.datingapp.model.Photo;
import org.springframework.stereotype.Component;

@Component
public class PhotoMapper {
    public PhotoDto toDto(Photo photo) {
        PhotoDto dto = new PhotoDto();
        dto.setId(photo.getId());
        dto.setUrl(photo.getUrl());
        return dto;
    }
}
