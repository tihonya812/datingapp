package com.tihonya.datingapp.service;

import java.time.Instant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class CacheEntry {
    private final Object value;
    private final Instant timestamp = Instant.now();
    private static final long CACHE_TTL_MS = 3L * 1000; // 3 минуты

    public boolean isExpired() {
        return Instant.now().toEpochMilli() -
                timestamp.toEpochMilli() > CACHE_TTL_MS;
    }
}
