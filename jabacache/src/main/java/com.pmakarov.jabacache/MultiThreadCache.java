package com.pmakarov.jabacache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Кэш
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class MultiThreadCache<K, V> implements JabaCache<K, V> {

    private static Logger logger = Logger.getLogger(MultiThreadCache.class);

    /**
     * Get method will return deep copy of cached object
     */
    protected final Boolean returnDeepCopy;

    /**
     * Кэш
     */
    protected final Map<K, JabaCacheValue<V>> cache;

    public MultiThreadCache(boolean returnDeepCopy) {
        this.returnDeepCopy = returnDeepCopy;
        this.cache = new ConcurrentHashMap<>();
    }

    /**
     * Запись в кэш
     *
     * @param key   ключ
     * @param value значение
     */
    @Override
    public void put(K key, V value) {
        if (key != null) {
            cache.put(key, new CacheValue<>(value));
        }
    }

    /**
     * Получение по ключу из кэша
     *
     * @param key ключ
     * @return значение
     */
    @Override
    public V get(K key) {
        JabaCacheValue<V> cacheValue = cache.get(key);
        if (cacheValue != null) {
            V value = cacheValue.getUserObject();
            // передаем копию объекта, чтобы его нельзя было поменять
            logger.debug(String.format("Get cached value by key: [%s]", key));
            return returnDeepCopy ? deepCopy(value) : value;
        } else {
            return null;
        }
    }

    /**
     * Удаление по ключу
     *
     * @param key - ключ
     */
    protected void remove(K key) {
        cache.remove(key);
    }

    /**
     * Удаление устаревшего кэша
     */
    @Override
    public void cleanUp() {
        cache.clear();
    }

    protected <T> T deepCopy(T obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(objectMapper.writeValueAsString(obj), (Class<T>) obj.getClass());
        } catch (IOException e) {
            logger.error(String.format("Cant getUserObject deep copy of %s; Exception: %s", obj.getClass(), e));
        }
        return null;
    }
}
