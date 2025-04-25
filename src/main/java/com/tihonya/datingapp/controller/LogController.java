package com.tihonya.datingapp.controller;

import com.tihonya.datingapp.exception.LogNotFoundException;
import com.tihonya.datingapp.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@RequiredArgsConstructor
@Tag(name = "Logs", description = "API для работы с логами")
public class LogController {
    private static final String LOG_DIRECTORY = "logs"; // Папка с логами
    private final LogService logService;

    @Operation(summary = "Загрузить лог по дате",
            description = "Позволяет скачать лог-файл по дате")
    @GetMapping("/{date}")
    public ResponseEntity<Resource> downloadLog(
            @Parameter(description = "Дата лог-файла в формате yyyy-MM-dd") @PathVariable String date) {
        String filename = String.format("datingapp-%s.log", date); // Пример: 2025-04-10
        Path filePath = Paths.get(LOG_DIRECTORY).resolve(filename);

        try {
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateLog() {
        String id = logService.generateLogAsync();
        return ResponseEntity.ok(id);
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<String> getStatus(@PathVariable String id) {
        return ResponseEntity.ok(logService.getStatus(id).name());
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        try {
            byte[] content = logService.getLogFile(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"log_" + id + ".txt\"")
                    .body(content);
        } catch (LogNotFoundException e) {
            throw new LogNotFoundException("Лог с ID " + id + " не найден");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/generate/period")
    public ResponseEntity<String> generateLogForPeriod(
            @RequestParam String from,
            @RequestParam String to) {
        try {
            LocalDate start = LocalDate.parse(from);
            LocalDate end = LocalDate.parse(to);

            String id = logService.generateLogForPeriodAsync(start, end);
            return ResponseEntity.ok(id);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Неверный формат даты. Используйте yyyy-MM-dd");
        }
    }
}
