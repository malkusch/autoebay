package de.malkusch.autoebay.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract class InMemoryRepository<K, V> {

    protected final Map<K, V> entities = new HashMap<>();

    public Optional<V> find(K key) {
        return Optional.ofNullable(entities.get(key));
    }

    public void delete(K key) {
        entities.remove(key);
    }
}
