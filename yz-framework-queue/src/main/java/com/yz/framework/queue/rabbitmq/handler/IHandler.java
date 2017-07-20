package com.yz.framework.queue.rabbitmq.handler;

public interface IHandler<T> {
	
    public void handler(T attachment);
}
