/**
 * yz-framework-cache 
 * AbstractCacheFactory.java 
 * com.yz.framework.cache 
 * TODO  
 * @author yazhong.qi
 * @date   2016年3月18日 上午11:47:25 
 * @version   1.0
 */

package com.yz.framework.cache;

import java.util.Date;

/**
 * ClassName:AbstractCacheFactory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月18日 上午11:47:25 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
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
