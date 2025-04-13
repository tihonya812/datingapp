package com.tihonya.datingapp.service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private static final int MAX_CACHE_SIZE = 3; // Максимальный размер кэша

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

    public void clearMatchesCache(Long profileId) {
        String prefix = "matches_" + profileId;
        cache.keySet().removeIf(key -> key.startsWith(prefix));
        LOGGER.info("🗑️ Очищены все мэтчи для профиля {}", profileId);
    }

    public void clearCache(String key) {
        cache.remove(key);
        LOGGER.info("🗑️ Кэш очищен для ключа: {}", key);
    }

    public void printCache() {
        LOGGER.info("📌 Текущее состояние кэша:");
        cache.forEach((key, entry) -> LOGGER.info("🔹 {} -> {}", key, entry.getValue()));
    }

    // Новый метод getSize, возвращающий текущий размер кэша
    public int getSize() {
        return cache.size();
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

    // 🕒 Периодическая очистка устаревших записей (каждые 3 секунды)
    @Scheduled(fixedRate = 30000)
    public void startCacheCleanupTask() {
        try {
            cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            LOGGER.info("🧹 Очистка устаревших записей из кэша");
            printCache();
        } catch (Exception e) {
            LOGGER.error("❌ Ошибка очистки кэша", e);
        }
    }
}