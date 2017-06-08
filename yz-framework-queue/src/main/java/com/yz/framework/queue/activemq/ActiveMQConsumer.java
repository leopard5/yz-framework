package com.yz.framework.queue.activemq;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.DisposableBean;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;

public class ActiveMQConsumer implements DisposableBean, Closeable {

	private static final Logger LOGGER = Logger.getLogger(ActiveMQConsumer.class);

	private Destination destination;
	private PooledConnectionFactory poolconnectionFactory;
	private Session session = null;
	private MessageConsumer messageConsumer = null;
	private Connection connection;
	private MessageListener messageListener;

	public void destroy() throws Exception {
		close();
	}

	public JobMessage reserve(int timeoutSeconds) {
		try {
			Message message = messageConsumer.receive(timeoutSeconds);
			return MessageUtil.fromMessage(message);
		} catch (JMSException e) {
			return null;
		}
	}

	public void close() {
		try {
			session.close();
		} catch (JMSException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		try {
			messageConsumer.close();
		} catch (JMSException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		poolconnectionFactory.stop();
	}

	public void start() {
		poolconnectionFactory.start();
		try {
			connection = poolconnectionFactory.createConnection();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			messageConsumer = session.createConsumer(destination);
			messageConsumer.setMessageListener(messageListener);
		} catch (JMSException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

	public boolean isConnectted() {
		return true;
	}

	public boolean delete(String messageId) {
		return false;
	}

	public boolean release(String messageId, long priority, int delaySeconds) {
		return false;
	}

	public boolean bury(String messageId, long priority) {
		// TODO 自动生成的方法存根
		return false;
	}

	public boolean touch(String messageId) {
		// TODO 自动生成的方法存根
		return false;
	}

	public int watch(String tubeName) {
		// TODO 自动生成的方法存根
		return 0;
	}

	public int ignore(String tubeName) {
		// TODO 自动生成的方法存根
		return 0;
	}

	public List<String> listTubesWatched() {
		// TODO 自动生成的方法存根
		return null;
	}

	public Map<String, String> statsMessage(String messageId) {
		// TODO 自动生成的方法存根
		return null;
	}

}
