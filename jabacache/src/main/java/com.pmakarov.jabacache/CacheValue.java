package com.pmakarov.jabacache;

/**
 * Wrapper для значения кэша
 *
 * @param <V> тип значения кэша
 */
public class CacheValue<V> implements JabaCacheValue<V> {

    /**
     * Значение
     */
    protected final V value;

    public CacheValue(V value) {
        this.value = value;
    }

    public V getUserObject() {
        return this.value;
    }

    @Override
    public String toString() {
        return "CacheValue [value=" + value + "]";
    }
}
