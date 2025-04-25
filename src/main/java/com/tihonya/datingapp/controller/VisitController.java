package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.service.VisitCounterService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/visit")
@RequiredArgsConstructor
public class VisitController {
    private final VisitCounterService visitCounterService;

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getAllStats() {
        return ResponseEntity.ok(visitCounterService.getAllCounts());
    }
}
