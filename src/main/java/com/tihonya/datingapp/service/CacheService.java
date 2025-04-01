package com.tihonya.datingapp.service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private static final int MAX_CACHE_SIZE = 100; // Максимальный размер кэша
    private static final long CLEANUP_INTERVAL_MS = 2 * 60 * 1000; // Интервал очистки (2 минуты)

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public CacheService() {
        startCacheCleanupTask();
    }

    public void saveToCache(String key, Object value) {
        if (cache.size() >= MAX_CACHE_SIZE) {
            removeOldestEntry();
        }
        cache.put(key, new CacheEntry(value));
        LOGGER.info("💾 Данные сохранены в кэш: {}", key);
    }

    public <T> T getFromCache(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry != null && !entry.isExpired()) {
            LOGGER.info("✅ Данные взяты из кэша: {}", key);
            return type.cast(entry.getValue());
        }
        cache.remove(key);
        LOGGER.info("❌ Кэш не найден или устарел для ключа: {}", key);
        return null;
    }

    //    public <T> T getFromCache(String key) {
    //        CacheEntry entry = cache.get(key);
    //        if (entry != null && !entry.isExpired()) {
    //            LOGGER.info("✅ Данные взяты из кэша: {}", key);
    //            return (T) entry.getValue();  // Приводим к нужному типу
    //        }
    //        cache.remove(key);
    //        LOGGER.info("❌ Кэш не найден или устарел для ключа: {}", key);
    //        return null;
    //    }

    public void clearCache(String key) {
        cache.remove(key);
        LOGGER.info("🗑️ Кэш очищен для ключа: {}", key);
    }

    public void printCache() {
        LOGGER.info("📌 Текущее состояние кэша:");
        cache.forEach((key, entry) -> LOGGER.info("🔹 {} -> {}", key, entry.getValue()));
    }

    private void removeOldestEntry() {
        Optional<Map.Entry<String, CacheEntry>> oldestEntry = cache.entrySet()
                .stream()
                .min(Comparator.comparing(e -> e.getValue().getTimestamp()));

        oldestEntry.ifPresent(entry -> {
            cache.remove(entry.getKey());
            LOGGER.info("🗑️ Удален самый старый кэш-ключ: {}", entry.getKey());
        });
    }

    private void startCacheCleanupTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Убираем устаревшие элементы из кэша
                cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
                LOGGER.info("🧹 Очистка устаревших записей из кэша");
                printCache();
            } catch (Exception e) {
                LOGGER.error("❌ Ошибка очистки кэша", e);
            }
        }, CLEANUP_INTERVAL_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
}

    //    private void startCacheCleanupTask() {
    //        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
    //            // Планируем задачу на регулярное выполнение
    //            scheduler.scheduleAtFixedRate(() -> {
    //                try {
    //                    // Убираем устаревшие элементы из кэша
    //                    cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    //                    LOGGER.info("🧹 Очистка устаревших записей из кэша");
    //                } catch (Exception e) {
    //                    LOGGER.error("❌ Ошибка очистки кэша", e);
    //                }
    //            }, CLEANUP_INTERVAL_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);
    //
    //
    //            Thread.sleep(Long.MAX_VALUE);  // Блокирует поток навсегда (или используйте ваше условие)
    //        } catch (InterruptedException e) {
    //            LOGGER.error("❌ Ошибка при ожидании завершения планировщика", e);
    //        }
    //    }





