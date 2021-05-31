package com.pmakarov.jabacache;

import org.apache.log4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * Кэш
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public final class MultiThreadTTLCache<K, V> extends MultiThreadCache<K, V> {

    private static Logger logger = Logger.getLogger(MultiThreadTTLCache.class);

    /**
     * Интервал проверки на устаревший кэш
     */
    private final long checkIntervalInSeconds;

    /**
     * Время жизни кэша в секундах
     */
    private final long timeToLiveInSeconds;

    public MultiThreadTTLCache(long timeToLiveInSeconds, long checkIntervalInSeconds, boolean returnDeepCopy) {
        super(returnDeepCopy);
        this.checkIntervalInSeconds = checkIntervalInSeconds;
        this.timeToLiveInSeconds = timeToLiveInSeconds;
        logger.debug(String.format("Creating ttl jabacache with params: [ttl=%s, checkInterval=%s]", timeToLiveInSeconds, checkIntervalInSeconds));
        initCleanupTask();
    }

    /**
     * Запуск потока для очистки истекшего кэша
     */
    private void initCleanupTask() {
        Thread cleanUpThread = new Thread(new CleanUpTask<>(this, checkIntervalInSeconds));
        cleanUpThread.setDaemon(true);
        cleanUpThread.start();
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
            cache.put(key, new TTLCacheValue<>(value));
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
        TTLCacheValue<V> cacheValue = (TTLCacheValue<V>) cache.get(key);
        if (cacheValue != null) {
            // передаем текущее время доступа к кэшу
            cacheValue.setLastAccessTimestamp(LocalDateTime.now());
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
        super.remove(key);
    }

    /**
     * Удаление устаревшего кэша
     */
    @Override
    public void cleanUp() {
        // берем ключи
        Set<K> keySet = cache.keySet();
        // засекаем тукущий таймстемп
        LocalDateTime now = LocalDateTime.now();
        // проверяем каждое значение в кэше
        for (K key : keySet) {
            TTLCacheValue<V> cacheValue = (TTLCacheValue<V>) cache.get(key);
            synchronized (cache) {
                if (cacheValue != null) {
                    // берем таймстемп последнего обращению к кэшу
                    LocalDateTime lastAccessTs = cacheValue
                            .getLastAccessTimestamp();
                    // сколько секунд прошло с момента последнего обращения
                    long elapsedTime = ChronoUnit.SECONDS.between(lastAccessTs, now);
                    // если прошло больше, чем было указано, то очищаем кэш по ключу
                    if (elapsedTime > this.timeToLiveInSeconds) {
                        this.remove(key);
                        logger.debug(String.format("Remove expired jabacache with key: [%s]", key));
                        Thread.yield();
                    }
                }
            }
        }
    }

    protected <T> T deepCopy(T obj) {
        return super.deepCopy(obj);
    }
}
