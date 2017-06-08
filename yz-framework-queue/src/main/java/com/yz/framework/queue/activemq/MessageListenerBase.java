package com.yz.framework.queue.activemq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;

public abstract class MessageListenerBase implements MessageListener {

	private final static Logger LOGGER = Logger.getLogger(MessageListenerBase.class);

	@Override
	public void onMessage(Message message) {
		try {
			JobMessage jobMessage = MessageUtil.fromMessage(message);
			processMessage(jobMessage);
		} catch (JMSException e) {
			LOGGER.error("onMessage", "转化 ActiveMQ Message为JobMessage发生异常", e);
		}
	}

	protected abstract void processMessage(JobMessage jobMessage);

}
