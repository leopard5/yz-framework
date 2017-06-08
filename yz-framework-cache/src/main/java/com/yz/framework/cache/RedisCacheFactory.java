/**
 * yz-framework-zookeeper 
 * LocalCacheFactory.java 
 * com.yz.framework.zookeeper 
 * TODO  
 * @author yazhong.qi
 * @date   2016年3月1日 上午11:55:38 
 * @version   1.0
 */

package com.yz.framework.cache;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import com.alibaba.fastjson.JSON;
import com.yz.framework.NameValue;
import com.yz.framework.logging.Logger;
import com.yz.framework.util.CompareUtil;
import com.yz.framework.zookeeper.ZK;

/**
 * ClassName:LocalCacheFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月1日 上午11:55:38 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
/**
 * TODO
 * 
 * @author yazhong.qi
 * @date 2016年3月1日 下午6:15:26
 * @version 1.0
 */
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
		}
		else if (relateExpiredTimeMs != null) {
			if (relateExpiredTimeMs.longValue() > 0) {
				opts.set(value, relateExpiredTimeMs.longValue(), TimeUnit.MILLISECONDS);
			}
		}
		else {
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

	public synchronized void destroy()
	{
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

	public synchronized void init() throws IOException, KeeperException, InterruptedException
	{
		// do nothing
	}

}
