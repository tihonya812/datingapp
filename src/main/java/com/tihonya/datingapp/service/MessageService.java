package com.tihonya.datingapp.service;

import com.tihonya.datingapp.dto.MessageDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.mapper.MessageMapper;
import com.tihonya.datingapp.model.Message;
import com.tihonya.datingapp.model.User;
import com.tihonya.datingapp.repository.MessageRepository;
import com.tihonya.datingapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public MessageDto sendMessage(MessageDto messageDto) {
        if (messageDto.getSenderId() == null) {
            throw new IllegalArgumentException("Sender ID must not be null");
        }
        if (messageDto.getReceiverId() == null) {
            throw new IllegalArgumentException("Receiver ID must not be null");
        }

        User sender = userRepository.findById(messageDto.getSenderId())
                .orElseThrow(() -> new NotFoundException(
                        "Sender not found with ID: " + messageDto.getSenderId()));
        User receiver = userRepository.findById(messageDto.getReceiverId())
                .orElseThrow(() -> new NotFoundException(
                        "Receiver not found with ID: " + messageDto.getReceiverId()));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setText(messageDto.getText());
        message.setTimestamp(Instant.now());
        message = messageRepository.save(message);

        return messageMapper.toDto(message);
    }

    @Transactional
    public List<MessageDto> getMessages(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(userId, userId);
        return messageMapper.toDtoList(messages);
    }
}