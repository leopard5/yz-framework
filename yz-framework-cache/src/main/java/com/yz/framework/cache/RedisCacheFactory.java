package com.yz.framework.cache;

import com.alibaba.fastjson.JSON;
import org.apache.zookeeper.KeeperException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RedisCacheFactory extends AbstractCacheFactory {

    private StringRedisTemplate redisTemplate;

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    private String cachePrefix;

    public boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Date expiredAt,
            final Long relateExpiredTimeMs,
            final Object... dependencies) {

        String redisKey = buildRedisKey(cacheObjectKey);
        if (cacheData == null) {
            remove(cacheObjectKey);
            return true;
        }
        String value = JSON.toJSONString(cacheData);
        BoundValueOperations<String, String> opts = redisTemplate.boundValueOps(redisKey);
        if (expiredAt != null) {
            long expireTimeInMs = expiredAt.getTime() - System.currentTimeMillis();
            if (expireTimeInMs > 0) {
                opts.set(value, expireTimeInMs, TimeUnit.MILLISECONDS);
            }
        } else if (relateExpiredTimeMs != null) {
            if (relateExpiredTimeMs.longValue() > 0) {
                opts.set(value, relateExpiredTimeMs.longValue(), TimeUnit.MILLISECONDS);
            }
        } else {
            opts.set(value);
        }
        return true;
    }

    private String buildRedisKey(String cacheObjectKey) {
        return cachePrefix + cacheObjectKey;
    }

    public boolean clear() {
        redisTemplate.delete(redisTemplate.keys(cachePrefix + "*"));
        return true;
    }

    public synchronized void destroy() {
        try {
            clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void expire(Object dependency) {
        throw new UnsupportedOperationException("不支持，请示用方法 remove(cacheObjectKey)");
    }

    public <T> T getObject(String cacheObjectKey) {
        throw new UnsupportedOperationException("不支持，请示用方法 getObject(String cacheObjectKey, Class<T> clazz)");
    }

    public <T> T getObject(String cacheObjectKey, Class<T> clazz) {

        String value = redisTemplate.boundValueOps(buildRedisKey(cacheObjectKey)).get();
        if (null == value) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }

    @Override
    public void remove(String cacheObjectKey) {
        redisTemplate.delete(buildRedisKey(cacheObjectKey));
    }

    public synchronized void init() throws IOException, KeeperException, InterruptedException {
        // do nothing
    }

}
