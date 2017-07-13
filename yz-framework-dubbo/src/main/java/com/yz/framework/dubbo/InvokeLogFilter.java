package com.yz.framework.dubbo;

import com.alibaba.dubbo.rpc.*;

import java.net.InetSocketAddress;

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
