package com.yz.framework.redis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RedisLock implements Serializable {

	private static final long serialVersionUID = 2110859698214399370L;
	private static final int DEFAULT_TIME_OUT = 120 * 1000;
	private static final long RETRY_LOCK_TIME_MILLISECONDS = 50;
	private String timeout;
	private boolean hasLock;
	private StringRedisTemplateEX redisTemplate;
	private String value;

	private List<String> keys = new ArrayList<String>(1);

	public RedisLock(StringRedisTemplateEX redisTemplate, String key) {
		this(redisTemplate, key, DEFAULT_TIME_OUT);
	}

	public RedisLock(StringRedisTemplateEX redisTemplate, String key, int timeOut) {

		keys.add(key);

		this.redisTemplate = redisTemplate;
		this.timeout = String.valueOf(timeOut);
		this.value = String.valueOf(System.currentTimeMillis());
	}

	public RedisLock(StringRedisTemplateEX redisTemplate, List<String> keys, int timeOut) {
		//去除重复的key
		Set<String> keySet = new HashSet<String>(keys);
		
		this.keys.addAll(keySet);
		this.redisTemplate = redisTemplate;
		this.timeout = String.valueOf(timeOut);
		this.value = String.valueOf(System.currentTimeMillis());
	}

	public RedisLock(StringRedisTemplateEX redisTemplate, List<String> keys) {
		this(redisTemplate, keys, DEFAULT_TIME_OUT);
	}

	public boolean lock() {
		try {
			while (!acquireLock()) {
				Thread.sleep(RETRY_LOCK_TIME_MILLISECONDS);
			}
		} catch (InterruptedException e) {
			hasLock = false;
		}
		return hasLock;
	}

	public boolean tryLock() {
		return acquireLock();
	}

	private boolean acquireLock() {
		hasLock = redisTemplate.setIfAbsent(keys, value, timeout);
		return hasLock;
	}

	public void unLock() {
		try {
			if (hasLock) {
				redisTemplate.deleteIfEqual(keys, value);
				hasLock = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
