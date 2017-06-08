/**
 * yz-framework-core 
 * RequestIdThreadLocal.java 
 * com.yz.framework.logging 
 * TODO  
 * @author yazhong.qi
 * @date   2016年5月5日 下午1:47:13 
 * @version   1.0
 */

package com.yz.framework.logging;

import java.util.UUID;

/**
 * ClassName:RequestIdThreadLocal <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月5日 下午1:47:13 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class RequestIdThreadLocal extends ThreadLocal<String> {
	@Override
	public String get() {
		String requestId = super.get();
		if (null == requestId) {
			requestId = UUID.randomUUID().toString();
			set(requestId);
		}
		return requestId;
	}
}
