package com.tihonya.datingapp.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.*;
import java.util.concurrent.CountDownLatch;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LogServiceTest {

    private LogService logService;

    @BeforeEach
    void setUp() {
        logService = new LogService();
    }

    @Test
    void testGenerateLogForPeriodAsync_success() throws Exception {
        // Создаём фейковый лог-файл для периода
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Path logFilePath = Path.of("logs", "datingapp-" + today.format(formatter) + ".log");
        Files.createDirectories(logFilePath.getParent());
        Files.writeString(logFilePath, "Test log content");

        String id = logService.generateLogForPeriodAsync(today, today);

        // Ждем, пока лог будет готов (но максимум 30 секунд)
        waitForLogToBeReady(id);

        assertEquals(LogService.LogStatus.READY, logService.getStatus(id));

        byte[] fileContent = logService.getLogFile(id);

        assertTrue(new String(fileContent).contains("Test log content"));
    }

    @Test
    void testGetStatus_notFound() {
        assertEquals(LogService.LogStatus.NOT_FOUND, logService.getStatus("nonexistent-id"));
    }

    @Test
    void testGetLogFile_notReady() {
        String id = "some-random-id";
        Exception exception = assertThrows(IOException.class, () -> logService.getLogFile(id));
        assertTrue(exception.getMessage().contains("Файл ещё не готов"));
    }

    private void waitForLogToBeReady(String id) {
        try (ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor()) {
            final CountDownLatch latch = new CountDownLatch(1);

            // Планируем задачу на периодическую проверку статуса
            executorService.scheduleWithFixedDelay(() -> {
                if (logService.getStatus(id) == LogService.LogStatus.READY) {
                    latch.countDown(); // Лог готов, сигнализируем об этом
                }
            }, 0, 1, TimeUnit.SECONDS); // Проверка каждую секунду

            // Ожидаем завершения или истечения таймаута
            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new TimeoutException("Лог не был готов в течение 30 секунд");
            }
        } catch (InterruptedException e) {
            // Если поток был прерван, прерываем выполнение
            Thread.currentThread().interrupt();
            throw new RuntimeException("Ожидание было прервано", e);
        } catch (TimeoutException e) {
            // Если истекло время ожидания
            throw new RuntimeException("Лог не был готов в течение 30 секунд", e);
        }
    }
}
