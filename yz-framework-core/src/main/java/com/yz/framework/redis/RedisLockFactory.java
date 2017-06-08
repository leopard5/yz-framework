/**
 * yz-framework-core 
 * RedisLockFactory.java 
 * com.yz.framework.redis 
 * TODO  
 * @author yazhong.qi
 * @date   2016年3月18日 下午3:40:47 
 * @version   1.0
 */

package com.yz.framework.redis;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * ClassName:RedisLockFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月18日 下午3:40:47 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class RedisLockFactory implements InitializingBean {

	private StringRedisTemplateEX redisTemplate;

	public StringRedisTemplateEX getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(StringRedisTemplateEX redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisLockFactory() {

	}

	public RedisLockFactory(StringRedisTemplateEX redisTemplate) {
		this.setRedisTemplate(redisTemplate);
	}

	private static RedisLockFactory singlton;

	@Override
	public void afterPropertiesSet() throws Exception {
		singlton = this;
	}

	private RedisLock buildLock(String key) {
		return new RedisLock(redisTemplate, key);
	}

	private RedisLock buildLock(List<String> keys) {
		return new RedisLock(redisTemplate, keys);
	}

	private RedisLock buildLock(String key, int expire) {
		return new RedisLock(redisTemplate, key, expire);
	}

	private RedisLock buildLock(List<String> keys, int expire) {
		return new RedisLock(redisTemplate, keys, expire);
	}

	public static RedisLock buildRedisLock(String key) {
		return singlton.buildLock(key);
	}

	public static RedisLock buildRedisLock(String key, int expire) {
		return singlton.buildLock(key, expire);
	}

	public static RedisLock buildRedisLock(List<String> keys) {
		return singlton.buildLock(keys);
	}

	public static RedisLock buildRedisLock(List<String> keys, int expire) {
		return singlton.buildLock(keys, expire);
	}

}
