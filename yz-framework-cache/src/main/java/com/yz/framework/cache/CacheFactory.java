package com.yz.framework.cache;

import java.util.Date;

public interface CacheFactory {

    <T> T getObject(String cacheObjectkey);

    <T> T getObject(String cacheObjectkey, Class<T> clazz);

    boolean add(
            final String cacheObjectKey,
            final Object cacheData
    );

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Object... dependencies
    );

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Date expiredAt
    );

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Long relativeExpiredMs
    );

    boolean add(
            final String cacheObjectKey,
            final Object cacheData,
            final Date expiredAt,
            final Long relativeExpiredMs,
            final Object... dependencies
    );

    void expire(Object dependency);

    void remove(String cacheObjectKey);

    boolean clear();
}
