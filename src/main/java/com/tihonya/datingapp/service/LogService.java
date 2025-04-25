package com.tihonya.datingapp.service;

import com.tihonya.datingapp.exception.LogNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LogService {
    private final Map<String, LogStatus> logStatusMap = new ConcurrentHashMap<>();
    private final Map<String, Path> logFiles = new ConcurrentHashMap<>();
    private final Executor executor = Executors.newCachedThreadPool();
    private final Path logsDir = Paths.get("logs"); // <-- путь к logs в корне проекта

    public LogService() {
        try {
            Files.createDirectories(logsDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать папку logs", e);
        }
    }

    public String generateLogAsync() {
        String id = UUID.randomUUID().toString();
        logStatusMap.put(id, LogStatus.IN_PROGRESS);

        executor.execute(() -> {
            try {
                Thread.sleep(20_000); // <-- задержка 10 секунд

                Path logPath = logsDir.resolve("log_" + id + ".txt");
                Files.writeString(logPath, "Лог-файл сгенерирован: " + Instant.now());

                logFiles.put(id, logPath);
                logStatusMap.put(id, LogStatus.READY);
            } catch (Exception e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Ошибка при генерации лог-файла", e);
            }
        });

        return id;
    }

    public LogStatus getStatus(String id) {
        return logStatusMap.getOrDefault(id, LogStatus.NOT_FOUND);
    }

    public byte[] getLogFile(String id) throws IOException {
        if (logStatusMap.get(id) == LogStatus.READY) {
            return Files.readAllBytes(logFiles.get(id));
        }
        throw new LogNotFoundException("Файл ещё не готов или не существует");
    }

    public enum LogStatus {
        IN_PROGRESS, READY, FAILED, NOT_FOUND
    }

    public String generateLogForPeriodAsync(LocalDate startDate, LocalDate endDate) {
        String id = UUID.randomUUID().toString();
        logStatusMap.put(id, LogStatus.IN_PROGRESS);

        executor.execute(() -> {
            try {
                Thread.sleep(20_000); // задержка 10 сек

                StringBuilder logs = new StringBuilder();

                Path logsDir = Paths.get("logs"); // путь к папке logs на верхнем уровне
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    String fileName = "datingapp-" + current.format(formatter) + ".log";
                    Path filePath = logsDir.resolve(fileName);

                    if (Files.exists(filePath)) {
                        logs.append("===== ").append(fileName).append(" =====\n");
                        logs.append(Files.readString(filePath)).append("\n");
                    }

                    current = current.plusDays(1);
                }

                if (logs.isEmpty()) {
                    logStatusMap.put(id, LogStatus.FAILED);
                    return;
                }

                Path mergedLog = logsDir.resolve("log_period_" + id + ".log");
                Files.writeString(mergedLog, logs.toString());

                logFiles.put(id, mergedLog);
                logStatusMap.put(id, LogStatus.READY);
            } catch (Exception e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Ошибка при генерации лог-файла", e);
            }
        });
        return id;
    }
}
