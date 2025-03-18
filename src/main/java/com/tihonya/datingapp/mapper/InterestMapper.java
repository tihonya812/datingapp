package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.model.Interest;
import org.springframework.stereotype.Component;

@Component
public class InterestMapper {
    public InterestDto toDto(Interest interest) {
        InterestDto dto = new InterestDto();
        dto.setId(interest.getId());
        dto.setName(interest.getName());
        return dto;
    }
}

