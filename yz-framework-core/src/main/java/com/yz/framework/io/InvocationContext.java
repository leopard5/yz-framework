/**
 * yz-framework-core 
 * InvocationContext.java 
 * com.yz.framework.io 
 * TODO  
 * @author yazhong.qi
 * @date   2016年2月3日 下午3:05:22 
 * @version   1.0
 */

package com.yz.framework.io;

import org.springframework.beans.factory.InitializingBean;

/**
 * ClassName:InvocationContext <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年2月3日 下午3:05:22 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class InvocationContext {

	private static final InvokeUniqueIdHolder invokeUniqueIdHolder = new InvokeUniqueIdHolder();

	public static String getInvokeUniqueId()
	{
		return invokeUniqueIdHolder.get();
	}

	public static void close() {
		invokeUniqueIdHolder.remove();
	}

	static class InvokeUniqueIdHolder extends ThreadLocal<String> {
		@Override
		protected String initialValue() {
			return String.valueOf(System.nanoTime());
		}
	}
}
