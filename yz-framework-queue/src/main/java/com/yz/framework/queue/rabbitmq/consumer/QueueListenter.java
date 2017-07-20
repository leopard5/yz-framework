package com.yz.framework.queue.rabbitmq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yz.framework.queue.rabbitmq.event.EventMessage;
import com.yz.framework.queue.rabbitmq.handler.HandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;

import java.nio.charset.Charset;

public class QueueListenter implements ChannelAwareMessageListener {
    private static Logger logger = LoggerFactory.getLogger(QueueListenter.class);

    public final static Charset UTF8 = Charset.forName("UTF-8");
    private Charset charset = UTF8;
    private HandlerFactory factory;

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        byte[] body = message.getBody();
        String source = new String(body);
        EventMessage em = JSONObject.parseObject(source, EventMessage.class);
        Object obj = JSONObject.parseObject(em.getAttachment(), em.getClazz());
        factory.handler(em.getRecipients(), obj);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息成功消费
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        QueueListenter.logger = logger;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public HandlerFactory getFactory() {
        return factory;
    }

    public void setFactory(HandlerFactory factory) {
        this.factory = factory;
    }

    public static Charset getUtf8() {
        return UTF8;
    }

}
