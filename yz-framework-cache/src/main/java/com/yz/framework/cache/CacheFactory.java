/**
 * yz-framework-cache 
 * CacheFactory.java 
 * com.yz.framework.zookeeper 
 * TODO  
 * @author yazhong.qi
 * @date   2016年3月2日 下午2:43:36 
 * @version   1.0
 */

package com.yz.framework.cache;

import java.sql.Time;
import java.util.Date;

/**
 * ClassName:CacheFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月2日 下午2:43:36 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
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

	/**
	 * 清楚所有缓存
	 * 
	 * @return boolean
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年3月2日 下午2:49:51
	 */
	boolean clear();
}
