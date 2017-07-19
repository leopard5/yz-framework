package com.yz.framework.mq.handler;

public interface IHandler<T> {
	
    public void handler(T attachment);
}
