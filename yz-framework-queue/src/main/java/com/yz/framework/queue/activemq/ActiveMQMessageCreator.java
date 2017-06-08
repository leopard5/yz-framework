package com.yz.framework.queue.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

import com.yz.framework.queue.JobMessage;

public class ActiveMQMessageCreator implements MessageCreator {

	private JobMessage jobMessage;
	private Message message;

	public ActiveMQMessageCreator(JobMessage jobMessage) {
		this.jobMessage = jobMessage;
	}

	public Message getMessage() {
		return message;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		if (message == null) {
			message = MessageUtil.createMessage(session, jobMessage);
		}
		return message;
	}

}
