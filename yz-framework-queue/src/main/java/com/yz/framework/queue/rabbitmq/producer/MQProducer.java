package com.yz.framework.queue.rabbitmq.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;

public interface MQProducer extends ConfirmCallback {


    /**
     * 生产者发送消息
     *
     * @param routingKey rabbitmq routkey,决定该消息走那个路由线路
     * @param recipients 消息接收者,是消息接收者的key
     * @param attachment 生产者投递的消息对象
     * @author yazhong.qi
     */
    public <T> void sendDataToQueue(String routingKey, String recipients, T attachment);

}
