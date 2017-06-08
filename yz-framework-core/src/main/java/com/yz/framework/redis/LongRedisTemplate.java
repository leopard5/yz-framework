package com.yz.framework.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class LongRedisTemplate extends RedisTemplate<String, Long> {
	public LongRedisTemplate() {
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		setKeySerializer(stringSerializer);
		setHashKeySerializer(stringSerializer);
	}
}
