package com.yz.framework.queue.rabbitmq.producer;

import com.alibaba.fastjson.JSONObject;
import com.yz.framework.queue.rabbitmq.event.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import java.util.UUID;

public class MQProducerImpl implements MQProducer {

    private static Logger logger = LoggerFactory.getLogger(MQProducerImpl.class);

    private RabbitTemplate rabbitTemplate;

    /**
     * 构造方法注入
     */
    public MQProducerImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        // rabbitTemplate如果为单例的话，那回调就是最后设置的内容
        rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info(" confirm id:" + correlationData + " ack:" + ack + " cause:" + cause);
    }

    @Override
    public <T> void sendDataToQueue(String routingKey, String recipients, T attachment) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        EventMessage message = new EventMessage();
        message.setRecipients(recipients);
        message.setClazz(attachment.getClass());
        String attachmentString = JSONObject.toJSONString(attachment);
        message.setAttachment(attachmentString);
        rabbitTemplate.convertAndSend(routingKey, message, correlationId);
    }

}
