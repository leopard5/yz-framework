package com.yz.framework.queue.rabbitmq.handler;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandlerFactory{
    private static Logger logger = LoggerFactory.getLogger(HandlerFactory.class);

    /**
     * 消息消费者集合，key:消息接收者，value：消息处理器
     */
    private Map<String, IHandler> handlerMap;
    
    public Map<String, IHandler> getHandlerMap() {
        return handlerMap;
    }

    public void setHandlerMap(Map<String, IHandler> handlerMap) {
        this.handlerMap = handlerMap;
    }

    public <T> void handler(String recipients,T attachment) {
        IHandler handler =  this.handlerMap.get(recipients);
        handler.handler(attachment);
    }
    
}
