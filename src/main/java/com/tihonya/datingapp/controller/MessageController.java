package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.MessageDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "API для работы с сообщениями")
public class MessageController {
    private static final Logger LOG = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private static final String INVALID_LOG_MESSAGE = "Log message: {}";

    @Operation(summary = "Отправить сообщение",
            description = "Отправляет сообщение от одного пользователя другому")
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        LOG.debug("Received messageDto: {}", messageDto); // Заменено System.out на логгер
        try {
            MessageDto savedMessage = messageService.sendMessage(messageDto);
            return ResponseEntity.ok(savedMessage);
        } catch (IllegalArgumentException e) {
            LOG.warn("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            LOG.warn("Not found error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            LOG.error("Internal server error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Operation(summary = "Получить сообщения", description = "Возвращает список сообщений для пользователя")
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(messageService.getMessages(userId));
        } catch (IllegalArgumentException e) {
            LOG.warn(INVALID_LOG_MESSAGE, e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            LOG.warn(INVALID_LOG_MESSAGE, e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }
}