package com.yz.framework.cache;

import com.yz.framework.logging.Logger;
import com.yz.framework.util.CompareUtil;
import org.apache.zookeeper.KeeperException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RedisInJVMCacheFactory extends AbstractCacheFactory {

    private static final Map<String, Object> cachedObjectsMap = new ConcurrentHashMap<String, Object>();
    private static final Map<String, String> dependecyMap = new ConcurrentHashMap<String, String>();
    private static final Logger LOGGER = Logger.getLogger(RedisInJVMCacheFactory.class);
    private final ArrayBlockingQueue<CachedData> updateDependencyQueue = new ArrayBlockingQueue<CachedData>(1000);
    private Thread updateDependencyWorker;
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean add(
            String cacheObjectKey,
            Object cacheData,
            Date expiredAt,
            Long relativeExpiredMs,
            Object... dependencies) {

        if (dependencies == null) {
            return false;
        }

        cachedObjectsMap.put(cacheObjectKey, cacheData);

        long expireMs = 0l;
        if (expiredAt != null) {
            expireMs = expiredAt.getTime() - System.currentTimeMillis();
        } else if (relativeExpiredMs != null) {
            expireMs = relativeExpiredMs.longValue();
        }
        CachedData cachedData = new CachedData(cacheObjectKey, dependencies[0].toString(), expireMs);
        updateDependecy(cachedData);
        return true;
    }

    private boolean updateDependecy(CachedData cachedData) {
        try {
            if (cachedData.expireMs > 0) {
                redisTemplate.boundValueOps(cachedData.key)
                        .set(cachedData.version, cachedData.expireMs, TimeUnit.MILLISECONDS);
            } else {
                redisTemplate.boundValueOps(cachedData.key).set(cachedData.version);
            }
            dependecyMap.put(cachedData.key, cachedData.version);
            return true;
        } catch (Exception e) {
            try {
                updateDependencyQueue.put(cachedData);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
        return false;

    }

    public boolean clear() {

        redisTemplate.delete(cachedObjectsMap.keySet());
        cachedObjectsMap.clear();
        dependecyMap.clear();
        return true;
    }

    public synchronized void destroy() {
        try {
            stopWorker();
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void expire(Object dependency) {
        // 不支持
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String cacheObjectkey) {

        String localVersion = dependecyMap.get(cacheObjectkey);
        String remoteVersion = redisTemplate.boundValueOps(cacheObjectkey).get();
        if (CompareUtil.equal(localVersion, remoteVersion)) {
            Object obj = cachedObjectsMap.get(cacheObjectkey);
            return obj == null ? null : (T) cachedObjectsMap.get(cacheObjectkey);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String cacheObjectkey, Class<T> clazz) {
        return getObject(cacheObjectkey);
    }

    private Thread getRefreshDependecyWorker() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    // 当线程收到中断通知，自动结束
                    while (!Thread.interrupted()) {
                        final CachedData cachedData = updateDependencyQueue.take();
                        if (!updateDependecy(cachedData)) {
                            Thread.sleep(20);
                        }
                    }
                } catch (InterruptedException e) {
                    // 该线程被终止
                    // do nothing
                }
            }
        });
    }

    public synchronized void init() throws IOException, KeeperException, InterruptedException {
        updateDependencyWorker = getRefreshDependecyWorker();
        updateDependencyWorker.start();
    }

    private void stopWorker() {
        try {
            if (updateDependencyWorker != null) {
                updateDependencyWorker.interrupt();
            }
        } catch (Exception e) {
            LOGGER.error("stopWorker", "停止缓存异步工作线程失败", e);
        }
    }

    @Override
    public void remove(String cacheObjectKey) {
        redisTemplate.delete(cacheObjectKey.toString());
        cachedObjectsMap.remove(cacheObjectKey);
        dependecyMap.remove(cacheObjectKey);
    }

    static class CachedData {
        private String key;
        private String version;
        private long expireMs;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public long getExpireMs() {
            return expireMs;
        }

        public void setExpireMs(long expireMs) {
            this.expireMs = expireMs;
        }

        public CachedData(String key, String version, long expireMs) {
            this.setExpireMs(expireMs);
            this.setKey(key);
            this.setVersion(version);
        }
    }
}
