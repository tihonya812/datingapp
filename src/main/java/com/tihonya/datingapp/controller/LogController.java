package com.tihonya.datingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
@Tag(name = "Logs", description = "API для работы с логами")
public class LogController {
    private static final String LOG_DIRECTORY = "logs"; // Папка с логами

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
}
