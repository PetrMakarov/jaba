package com.pmakarov.jabacache;

import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Поток для очистки устаревшего кэша
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class CleanUpTask<K, V> implements Runnable {

    private Logger logger = Logger.getLogger(CleanUpTask.class);

    /**
     * Инстанс кэша
     */
    private final JabaCache<K, V> instance;

    /**
     * Интервал проверки в секундах
     */
    private long timeoutSec;

    public CleanUpTask(JabaCache<K, V> instance, long timeoutSec) {
        logger.debug("Creating jabacache clean task");
        this.instance = instance;
        this.timeoutSec = timeoutSec;
    }

    @Override
    public void run() {
        // проверка кэша на устаревшие значения каждые timeoutSec секунд
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(timeoutSec);
                instance.cleanUp();
            } catch (InterruptedException ie) {
                logger.error(String.format("Failed to timeout jabacache clean up task by %s sec", timeoutSec));
                // TODO: возможно здесь нужно будет перезапускать таск, если sleep будет фейлиться много раз подряд
                Thread.currentThread().interrupt();
            }
        }

    }
}
