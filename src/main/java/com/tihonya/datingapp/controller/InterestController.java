package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interests")
@RequiredArgsConstructor
@Tag(name = "Interests", description = "API для работы с интересами")
public class InterestController {
    private final InterestService interestService;

    @Operation(summary = "Создание нового интереса",
            description = "Создает новый интерес и возвращает его данные")
    @PostMapping
    public ResponseEntity<InterestDto> createInterest(
            @Parameter(description = "Данные интереса")
            @Valid @RequestBody InterestDto interestDto) {
        InterestDto createdInterest = interestService.createInterest(interestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInterest);
    }

    @Operation(summary = "Получение всех интересов", description = "Возвращает список всех интересов")
    @GetMapping
    public List<InterestDto> getAllInterests() {
        return interestService.getAllInterests();
    }

    @Operation(summary = "Получение интереса по ID", description = "Возвращает интерес по указанному ID")
    @GetMapping("/{id}")
    public ResponseEntity<InterestDto> getInterestById(
            @Parameter(description = "Идентификатор интереса")
            @PathVariable Long id) {
        InterestDto interestDto = interestService.getInterestById(id);
        return ResponseEntity.ok(interestDto);
    }

    @Operation(summary = "Обновление интереса", description = "Обновляет интерес по указанному ID")
    @PutMapping("/{id}")
    public ResponseEntity<InterestDto> updateInterest(
            @Parameter(description = "Идентификатор интереса")
            @PathVariable Long id,
            @Parameter(description = "Данные для обновления интереса")
            @RequestBody @Valid InterestDto interestDto) {
        InterestDto updatedInterest = interestService.updateInterest(id, interestDto);
        return ResponseEntity.ok(updatedInterest);
    }

    @Operation(summary = "Удаление интереса", description = "Удаляет интерес по указанному ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterest(
            @Parameter(description = "Идентификатор интереса")
            @PathVariable Long id) {
        interestService.deleteInterest(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Создание нескольких интересов",
            description = "Создает несколько интересов и возвращает их данные")
    @PostMapping("/bulk")
    public ResponseEntity<List<InterestDto>> createInterestsBulk(
            @Parameter(description = "Список интересов")
            @Valid @RequestBody List<InterestDto> interestDtos) {
        List<InterestDto> createdInterests = interestService.createInterests(interestDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInterests);
    }
}
