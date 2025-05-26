package com.tihonya.datingapp.mapper;

import com.tihonya.datingapp.dto.MessageDto;
import com.tihonya.datingapp.model.Message;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public MessageDto toDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setText(message.getText());
        dto.setTimestamp(message.getTimestamp().toString());
        return dto;
    }

    public List<MessageDto> toDtoList(List<Message> messages) {
        return messages.stream().map(this::toDto).collect(Collectors.toList());
    }
}