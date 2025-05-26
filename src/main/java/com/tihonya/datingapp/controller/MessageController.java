package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.MessageDto;
import com.tihonya.datingapp.exception.NotFoundException;
import com.tihonya.datingapp.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final MessageService messageService;

    @Operation(summary = "Отправить сообщение",
            description = "Отправляет сообщение от одного пользователя другому")
    @PostMapping
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageDto messageDto) {
        System.out.println("Received messageDto: " + messageDto); // Отладка
        try {
            MessageDto savedMessage = messageService.sendMessage(messageDto);
            return ResponseEntity.ok(savedMessage);
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (NotFoundException e) {
            System.err.println("Not found error: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            System.err.println("Internal server error: " + e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @Operation(summary = "Получить сообщения", description = "Возвращает список сообщений для пользователя")
    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageDto>> getMessages(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(messageService.getMessages(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}