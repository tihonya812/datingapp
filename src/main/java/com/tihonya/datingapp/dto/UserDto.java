package com.tihonya.datingapp.dto;

import java.util.List;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String role;
    private ProfileDto profile;
    private List<InterestDto> interests;
}

