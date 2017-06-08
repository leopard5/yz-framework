package com.yz.framework.queue.activemq;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageFormatException;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.transaction.NotSupportedException;

import org.apache.activemq.ScheduledMessage;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.util.StringUtil;

public abstract class MessageUtil {

	public static Message createMessage(Session session, JobMessage jobMessage) throws JMSException {
		Message message = null;
		Object body = jobMessage.getBody();
		if (body instanceof String) {
			message = session.createTextMessage((String) body);
		} else if (body instanceof byte[]) {
			BytesMessage bytesMessage = session.createBytesMessage();
			bytesMessage.writeBytes((byte[]) body);
			message = bytesMessage;
		}

		if (jobMessage.getProperties() != null && jobMessage.getProperties().size() > 0) {
			// remove schedule id for re-shedule
			jobMessage.getProperties().remove(ScheduledMessage.AMQ_SCHEDULED_ID);
			Set<String> headers = jobMessage.getProperties().keySet();
			for (String key : headers) {
				message.setObjectProperty(key, jobMessage.getProperties().get(key));
			}
		}

		if (StringUtils.hasText(jobMessage.getProducedBy())) {
			message.setStringProperty(JobMessage.PRODUCE_BY, jobMessage.getProducedBy());
		}
		if (jobMessage.getDelaySeconds() > 0) {
			long delayMS = jobMessage.getDelaySeconds() * 1000;
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delayMS);

		}
		message.setLongProperty(JobMessage.CREATED_TIME_MS, System.currentTimeMillis());
		message.setJMSPriority((int) jobMessage.getPriority());

		return message;
	}

	@SuppressWarnings("unchecked")
	public static JobMessage fromMessage(Message message) throws JMSException {
		JobMessage jobMessage = new JobMessage();
		jobMessage.setMessageId(message.getJMSMessageID());
		jobMessage.setPriority(message.getJMSPriority());
		jobMessage.setTimestamp(message.getJMSTimestamp());
		Enumeration<String> propertyNames = message.getPropertyNames();
		String name = null;
		while (propertyNames.hasMoreElements() && (name = propertyNames.nextElement()) != null) {
			jobMessage.getProperties().put(name, message.getObjectProperty(name));
		}
		if (message instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) message;
			jobMessage.setBody(textMessage.getText());
		} else if (message instanceof MapMessage) {
			MapMessage mapMessage = (MapMessage) message;
			Enumeration<String> mapNames = mapMessage.getMapNames();
			Map<String, Object> body = new HashMap<String, Object>();
			while (mapNames.hasMoreElements() && (name = mapNames.nextElement()) != null) {
				body.put(name, mapMessage.getObject(name));
			}
			jobMessage.setBody(JSON.toJSONString(body));
		} else if (message instanceof BytesMessage) {
			BytesMessage bytesMessage = (BytesMessage) message;
			byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
			bytesMessage.readBytes(bytes);
			jobMessage.setBody(new String(bytes));
		} else if (message instanceof StreamMessage) {
			throw new MessageFormatException("StreamMessage is not supportted!");
			// StreamMessage streamMessage = (StreamMessage) message;
			// jobMessage.setBody(streamMessage.readObject());
		}

		return jobMessage;
	}
}
