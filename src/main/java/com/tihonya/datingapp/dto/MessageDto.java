package com.tihonya.datingapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDto {
    private Long id;
    @NotNull(message = "Sender ID must not be null")
    private Long senderId;
    @NotNull(message = "Receiver ID must not be null")
    private Long receiverId;
    @NotNull(message = "Text must not be null")
    private String text;
    private String timestamp;
}