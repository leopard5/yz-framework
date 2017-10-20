package com.yz.framework.queue.rabbitmq.event;

public interface IHandler<T> {
	
    public void handler(T attachment);
}
