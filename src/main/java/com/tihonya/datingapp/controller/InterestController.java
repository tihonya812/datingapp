package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.dto.InterestDto;
import com.tihonya.datingapp.service.InterestService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/interests")
public class InterestController {
    private final InterestService interestService;

    // Создание нового интереса
    @PostMapping
    public ResponseEntity<InterestDto> createInterest(@RequestBody InterestDto interestDto) {
        InterestDto createdInterest = interestService.createInterest(interestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInterest);
    }

    // Получение всех интересов
    @GetMapping
    public List<InterestDto> getAllInterests() {
        return interestService.getAllInterests();
    }

    // Получение интереса по ID
    @GetMapping("/{id}")
    public ResponseEntity<InterestDto> getInterestById(@PathVariable Long id) {
        InterestDto interestDto = interestService.getInterestById(id);
        return ResponseEntity.ok(interestDto);
    }

    // Обновление интереса
    @PutMapping("/{id}")
    public ResponseEntity<InterestDto> updateInterest(@PathVariable Long id,
                                                      @RequestBody InterestDto interestDto) {
        InterestDto updatedInterest = interestService.updateInterest(id, interestDto);
        return ResponseEntity.ok(updatedInterest);
    }

    // Удаление интереса
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInterest(@PathVariable Long id) {
        interestService.deleteInterest(id);
        return ResponseEntity.noContent().build();
    }
}
