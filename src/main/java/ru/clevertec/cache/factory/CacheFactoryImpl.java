package ru.clevertec.cache.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.clevertec.cache.Cache;
import ru.clevertec.cache.impl.LFUCache;
import ru.clevertec.cache.impl.LRUCache;

@Component
public class CacheFactoryImpl<K, V> implements CacheFactory<K, V> {

    @Value("${cache.algorithm}")
    private String algorithm;

    @Value("${cache.maxSize}")
    private int maxSize;

    @Override
    public Cache<K, V> createCache() {
        Cache<K, V> cache;
        switch (algorithm) {
            case "LRU" -> cache = new LRUCache<>(maxSize);
            case "LFU" -> cache = new LFUCache<>(maxSize);
            default -> throw new IllegalStateException("Unexpected value: " + algorithm);
        }
        return cache;
    }
}
