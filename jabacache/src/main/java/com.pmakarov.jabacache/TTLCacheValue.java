package com.pmakarov.jabacache;

import java.time.LocalDateTime;

/**
 * Wrapper для значения кэша, чтобы хранить время последнего обращения к значению
 *
 * @param <V> тип значения кэша
 */
public class TTLCacheValue<V> extends CacheValue<V> {

    /**
     * Время последнего обращения
     */
    private LocalDateTime lastAccessTimestamp;

    public TTLCacheValue(V value) {
        super(value);
        this.lastAccessTimestamp = LocalDateTime.now();
    }

    public LocalDateTime getLastAccessTimestamp() {
        return lastAccessTimestamp;
    }

    public void setLastAccessTimestamp(LocalDateTime lastAccessTimestamp) {
        this.lastAccessTimestamp = lastAccessTimestamp;
    }

    public V getUserObject() {
        return super.getUserObject();
    }

    @Override
    public String toString() {
        return "CacheValue [value=" + value + ", lastAccessTimestamp="
                + lastAccessTimestamp + "]";
    }
}
