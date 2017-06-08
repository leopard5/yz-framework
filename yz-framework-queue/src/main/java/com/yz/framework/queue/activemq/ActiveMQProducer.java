package com.yz.framework.queue.activemq;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jms.core.JmsTemplate;

import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Producer;

public class ActiveMQProducer implements Producer, DisposableBean {

	private static final Logger LOGGER = Logger.getLogger(ActiveMQConsumer.class);

	public JmsTemplate getJmsTemplate() {
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	private String destination;

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
		defaultDestination = new ActiveMQQueue(this.destination);
	}

	private Destination defaultDestination = null;
	private JmsTemplate jmsTemplate;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private String mqDescription;

	@Override
	public synchronized void start() {
		// do nothing
	}

	@Override
	public synchronized void close() {

		// do nothing
	}

	@Override
	public String produce(JobMessage message) throws Throwable {
		try {
			Message message2 = doProduceMessage(message, defaultDestination);
			return message2.getJMSMessageID();
		} catch (JMSException e) {
			LOGGER.error("produce", "往消息队列发送失败", mqDescription, message.toString(), e);
			throw e;
		}
	}

	private Message doProduceMessage(JobMessage jobMessage, Destination destination) throws JMSException {
		jobMessage.setProducedBy(this.name);
		ActiveMQMessageCreator messageCreator = new ActiveMQMessageCreator(jobMessage);
		jmsTemplate.send(destination, messageCreator);
		return messageCreator.getMessage();
	}

	@Override
	public String produce(JobMessage jobMessage, String queueName) throws Throwable {
		Destination destination = new ActiveMQQueue(queueName);
		Message message2 = doProduceMessage(jobMessage, destination);
		return message2.getJMSMessageID();
	}

	@Override
	public String publish(JobMessage jobMessage, String topicName) throws Throwable {

		Destination topicDestination = new ActiveMQTopic(topicName);
		Message message2 = doProduceMessage(jobMessage, topicDestination);
		return message2.getJMSMessageID();
	}

	@Override
	public String publish(JobMessage jobMessage) throws Throwable {
		Message message2 = doProduceMessage(jobMessage, defaultDestination);
		return message2.getJMSMessageID();
	}

	@Override
	public void destroy() throws Exception {
		this.close();
	}

}
