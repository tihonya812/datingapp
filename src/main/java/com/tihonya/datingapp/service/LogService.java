package com.tihonya.datingapp.service;

import com.tihonya.datingapp.exception.LogServiceInitializationException;
import java.io.FileNotFoundException;
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
    private final Path logsDirectory = Paths.get("logs"); // <-- путь к logs в корне проекта

    public LogService() {
        try {
            Files.createDirectories(logsDirectory);
        } catch (IOException e) {
            log.error("Не удалось создать папку logs", e);
            throw new LogServiceInitializationException("Ошибка при инициализации LogService", e);
        }
    }

    public String generateLogAsync() {
        String id = UUID.randomUUID().toString();
        logStatusMap.put(id, LogStatus.IN_PROGRESS);

        executor.execute(() -> {
            try {
                Thread.sleep(20_000); // Задержка 20 секунд
                Path logPath = logsDirectory.resolve("log_" + id + ".txt");
                Files.writeString(logPath, "Лог-файл сгенерирован: " + Instant.now());
                logFiles.put(id, logPath);
                logStatusMap.put(id, LogStatus.READY);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // <-- корректная обработка InterruptedException
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Генерация лог-файла была прервана", ie);
            } catch (IOException e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Ошибка при генерации лог-файла", e);
            }
        });

        return id;
    }

    public String generateLogForPeriodAsync(LocalDate startDate, LocalDate endDate) {
        String id = UUID.randomUUID().toString();
        logStatusMap.put(id, LogStatus.IN_PROGRESS);

        executor.execute(() -> {
            try {
                Thread.sleep(20_000); // Задержка 20 секунд
                StringBuilder collectedLogs = new StringBuilder();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                LocalDate current = startDate;
                while (!current.isAfter(endDate)) {
                    String fileName = "datingapp-" + current.format(formatter) + ".log";
                    Path filePath = logsDirectory.resolve(fileName);

                    if (Files.exists(filePath)) {
                        collectedLogs.append("===== ").append(fileName).append(" =====\n");
                        collectedLogs.append(Files.readString(filePath)).append("\n");
                    }

                    current = current.plusDays(1);
                }

                if (collectedLogs.isEmpty()) {
                    logStatusMap.put(id, LogStatus.FAILED);
                    return;
                }

                Path mergedLog = logsDirectory.resolve("log_period_" + id + ".log");
                Files.writeString(mergedLog, collectedLogs.toString());
                logFiles.put(id, mergedLog);
                logStatusMap.put(id, LogStatus.READY);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // <-- корректная обработка InterruptedException
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Генерация периодического лог-файла была прервана", ie);
            } catch (IOException e) {
                logStatusMap.put(id, LogStatus.FAILED);
                log.error("Ошибка при сборе логов за период", e);
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
        throw new FileNotFoundException("Файл ещё не готов или не существует");
    }

    public enum LogStatus {
        IN_PROGRESS, READY, FAILED, NOT_FOUND
    }
}