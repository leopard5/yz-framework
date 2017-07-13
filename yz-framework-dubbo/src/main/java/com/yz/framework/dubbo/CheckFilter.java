package com.yz.framework.dubbo;

import com.alibaba.dubbo.rpc.*;
import com.yz.framework.data.MapperFactory;

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
