package com.example.ordinaMii.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final long ID_CACHE_SIZE = 500;
    private static final long SEARCH_CACHE_SIZE = 100;

    @Bean
    public CacheManager cacheManager() {

        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(List.of(
                buildCache("dishById", Duration.ofMinutes(30), ID_CACHE_SIZE),
                buildCache("dishSearch", Duration.ofMinutes(10), SEARCH_CACHE_SIZE),

                buildCache("tableById", Duration.ofMinutes(30), ID_CACHE_SIZE),
                buildCache("tableSearch", Duration.ofMinutes(10), SEARCH_CACHE_SIZE),

                buildCache("userById", Duration.ofMinutes(20), ID_CACHE_SIZE),
                buildCache("userSearch", Duration.ofMinutes(5), SEARCH_CACHE_SIZE),

                buildCache("orderById", Duration.ofMinutes(10), ID_CACHE_SIZE),
                buildCache("orderSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE),
                buildCache("myOrderSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE),
                buildCache("userOrderSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE),

                buildCache("reservationById", Duration.ofMinutes(10), ID_CACHE_SIZE),
                buildCache("reservationSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE),
                buildCache("reservationTableSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE),
                buildCache("myReservationSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE),

                buildCache("assistanceRequestById", Duration.ofMinutes(10), ID_CACHE_SIZE),
                buildCache("assistanceRequestSearch", Duration.ofMinutes(3), SEARCH_CACHE_SIZE)
        ));

        return cacheManager;
    }

    private CaffeineCache buildCache(String name, Duration duration, long maximumSize) {
        return new CaffeineCache(
                name,
                Caffeine.newBuilder()
                        .expireAfterWrite(duration)
                        .maximumSize(maximumSize)
                        .recordStats()
                        .build(),
                false
        );
    }
}