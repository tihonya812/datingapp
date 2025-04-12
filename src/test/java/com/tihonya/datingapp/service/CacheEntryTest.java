package com.tihonya.datingapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

class CacheEntryTest {

    private static final String TEST_VALUE = "test value";

    private CacheEntry cacheEntry;

    @BeforeEach
    void setUp() {
        cacheEntry = new CacheEntry(TEST_VALUE);
    }

    @Test
    void testCacheEntryCreation() {
        assertNotNull(cacheEntry, "CacheEntry should be created successfully");
        assertEquals(TEST_VALUE, cacheEntry.getValue(), "CacheEntry value should be correct");
        assertNotNull(cacheEntry.getTimestamp(), "Timestamp should not be null");
    }

    @Test
    void testCacheEntryNotExpiredImmediately() {
        // Проверяем, что объект не истек через мгновение после создания
        assertFalse(cacheEntry.isExpired(), "CacheEntry should not be expired immediately after creation");
    }

    @Test
    void testCacheEntryExpiresAfterTTL() throws InterruptedException {
        // Засыпаем больше времени, чем TTL, и проверяем истечение срока
        long ttlMs = 3L * 1000; // 3 минуты (в миллисекундах)

        // Ждем больше, чем TTL
        TimeUnit.MILLISECONDS.sleep(ttlMs + 1000); // ждем 1 секунду больше TTL

        assertTrue(cacheEntry.isExpired(), "CacheEntry should be expired after TTL");
    }

    @Test
    void testCacheEntryNotExpiredBeforeTTL() throws InterruptedException {
        // Засыпаем меньше времени, чем TTL
        long ttlMs = 3L * 1000; // 3 минуты (в миллисекундах)

        // Ждем только половину времени TTL
        TimeUnit.MILLISECONDS.sleep(ttlMs / 2);

        assertFalse(cacheEntry.isExpired(), "CacheEntry should not be expired before TTL");
    }

    @Test
    void testCacheEntryHasCorrectTimestamp() {
        Instant timestamp = cacheEntry.getTimestamp();
        assertNotNull(timestamp, "Timestamp should not be null");

        // Учитываем погрешность в миллисекундах, так как операция создания может занять немного времени
        Instant now = Instant.now();

        // Убедимся, что timestamp до текущего времени, с учетом небольшой погрешности
        assertTrue(timestamp.isBefore(now) || timestamp.equals(now), "Timestamp should be before or equal to current time");
    }

}