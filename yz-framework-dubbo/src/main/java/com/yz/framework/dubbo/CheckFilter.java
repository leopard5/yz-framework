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

import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.yz.framework.data.MapperFactory;

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
public class CheckFilter implements Filter {

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		invocation.getAttachments().put("uniqeInvokeId", String.valueOf(System.currentTimeMillis()));
		Result result = invoker.invoke(invocation);
		if (MapperFactory.checkAndRollbackTransaction()) {
			throw new RpcException("found un complete transaction");
		}
		return result;
	}
}
