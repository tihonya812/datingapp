package com.tihonya.datingapp.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_DIRECTORY = "logs"; // Папка с логами

    @GetMapping("/{date}")
    public ResponseEntity<Resource> downloadLog(@PathVariable String date) {
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
