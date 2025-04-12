package com.tihonya.datingapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {

    @InjectMocks
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        // Эта настройка может быть полезна для повторной инициализации кэша перед каждым тестом
    }

    @Test
    void testSaveAndGetFromCache() {
        String key = "testKey";
        String value = "testValue";

        // Сохранение в кэш
        cacheService.saveToCache(key, value);

        // Получение из кэша
        String cachedValue = cacheService.getFromCache(key, String.class);

        // Проверка, что кэш работает
        assertNotNull(cachedValue, "Значение должно быть получено из кэша");
        assertEquals(value, cachedValue, "Полученное значение из кэша не соответствует ожидаемому");
    }

    @Test
    void testCacheSizeLimit() {
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        String value = "value";

        cacheService.saveToCache(key1, value);
        cacheService.saveToCache(key2, value);
        cacheService.saveToCache(key3, value);

        // После того, как кэш переполнен, старые записи должны удаляться
        cacheService.saveToCache("key4", value);

        // Проверяем, что размер кэша не превышает максимальный
        assertEquals(3, cacheService.getSize(), "Размер кэша должен быть ограничен максимальным значением");
    }

    @Test
    void testClearCache() {
        String key = "testKey";
        String value = "testValue";

        cacheService.saveToCache(key, value);

        // Очищаем кэш для определённого ключа
        cacheService.clearCache(key);

        // Проверяем, что кэш очищен
        String cachedValue = cacheService.getFromCache(key, String.class);
        assertNull(cachedValue, "Значение должно быть удалено из кэша");
    }

    @Test
    void testClearMatchesCache() {
        String profileId = "1";
        String key1 = "matches_1_20_30";
        String key2 = "matches_1_25_35";
        String value = "testValue";

        cacheService.saveToCache(key1, value);
        cacheService.saveToCache(key2, value);

        // Очищаем все кэшированные ключи для данного профиля
        cacheService.clearMatchesCache(Long.valueOf(profileId));

        // Проверяем, что все кэшированные ключи для matches были очищены
        assertNull(cacheService.getFromCache(key1, String.class), "Значение должно быть удалено для ключа matches_1_20_30");
        assertNull(cacheService.getFromCache(key2, String.class), "Значение должно быть удалено для ключа matches_1_25_35");
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        String key = "testKey";
        String value = "testValue";

        cacheService.saveToCache(key, value);

        // Имитируем время истечения срока кэширования
        Thread.sleep(3000); // Допустим, кэш устареет через 2 секунды (CLEANUP_INTERVAL_MS)

        // Получение значения из кэша после его истечения
        String cachedValue = cacheService.getFromCache(key, String.class);

        // Проверяем, что значение больше не доступно из кэша после его истечения
        assertNull(cachedValue, "Значение должно быть удалено из кэша после истечения срока");
    }

    @Test
    void testRemoveOldestEntryThroughCacheSave() {
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        String value = "value";

        cacheService.saveToCache(key1, value);
        cacheService.saveToCache(key2, value);
        cacheService.saveToCache(key3, value);

        // Превышаем лимит кэша, что должно вызвать удаление самой старой записи
        cacheService.saveToCache("key4", value);

        // Проверяем, что самая старая запись (key1) была удалена
        assertNull(cacheService.getFromCache(key1, String.class), "Самая старая запись должна быть удалена из кэша");
        assertNotNull(cacheService.getFromCache(key2, String.class), "Новая запись должна остаться в кэше");
    }


    @Test
    void testPrintCache() {
        // Проверка, что метод printCache не вызывает исключений
        cacheService.printCache();
    }
}
