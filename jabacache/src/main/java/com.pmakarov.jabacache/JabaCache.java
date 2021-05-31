package com.pmakarov.jabacache;

/**
 * Кэш
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public interface JabaCache<K, V> {

    /**
     * Запись кэша
     *
     * @param key  ключ
     * @param item значение
     */
    void put(K key, V item);

    /**
     * Получение значения кэша по ключу
     *
     * @param key ключ
     * @return значение
     */
    V get(K key);

    /**
     * Очистка кэша
     */
    void cleanUp();
}
