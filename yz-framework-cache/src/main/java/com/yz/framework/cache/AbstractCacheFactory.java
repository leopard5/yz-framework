package com.yz.framework.cache;

import java.util.Date;

public abstract class AbstractCacheFactory implements CacheFactory {

    @Override
    public boolean add(String cacheObjectKey, Object cacheData) {

        return add(cacheObjectKey, cacheData, null, null);
    }

    @Override
    public boolean add(String cacheObjectKey, Object cacheData, Date expiredAt) {

        return add(cacheObjectKey, cacheData, expiredAt, null);
    }

    @Override
    public boolean add(String cacheObjectKey, Object cacheData, Long relativeExpiredMs) {

        return add(cacheObjectKey, cacheData, null, relativeExpiredMs);
    }

    @Override
    public boolean add(String cacheObjectKey, Object cacheData, Object... dependencies) {

        return add(cacheObjectKey, cacheData, null, null, dependencies);
    }

}
