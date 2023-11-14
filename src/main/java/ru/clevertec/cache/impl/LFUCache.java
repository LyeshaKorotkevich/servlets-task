package ru.clevertec.cache.impl;

import ru.clevertec.cache.Cache;

import java.util.Optional;

public class LFUCache<K, V> implements Cache<K, V> {
    @Override
    public void clean() {

    }

    @Override
    public Optional<V> get(K key) {
        return Optional.empty();
    }

    @Override
    public void put(K key, V value) {

    }

}
