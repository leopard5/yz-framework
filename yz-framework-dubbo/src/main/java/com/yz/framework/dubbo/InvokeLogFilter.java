/**
 * yz-gc-service 
 * InvokeLogFilter.java 
 * com.yz.gc.service.dubbo 
 * TODO  
 * @author yazhong.qi
 * @date   2015年10月22日 下午6:20:15 
 * @version   1.0
 */

package com.yz.framework.dubbo;

import java.net.InetSocketAddress;

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * ClassName:InvokeLogFilter <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年10月22日 下午6:20:15 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class InvokeLogFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		RpcContext ssContext = RpcContext.getContext();
		InetSocketAddress socketAddress = ssContext.getRemoteAddress();
		String hostName = "";
		String hostIp = "";
		if (socketAddress != null) {
			hostName = socketAddress.getAddress().getHostName();
			hostIp = socketAddress.getAddress().getHostAddress();
		}
		long requestTime = System.currentTimeMillis();
		Result result = null;
		RpcException rpcException = null;
		try {
			result = invoker.invoke(invocation);
			return result;

		} catch (RpcException ex) {
			rpcException = ex;
			throw rpcException;

		} finally {
			InvokeLogger.logInvoke(invoker, invocation, requestTime, result, hostName, hostIp, rpcException);
		}
	}
}
