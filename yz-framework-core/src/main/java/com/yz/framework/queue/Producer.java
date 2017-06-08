package com.yz.framework.queue;

public interface Producer {

	String produce(final JobMessage message) throws Throwable;

	String produce(final JobMessage message, final String queueName) throws Throwable;

	String publish(final JobMessage message, final String topicName) throws Throwable;

	String publish(final JobMessage jobMessage) throws Throwable;

	void start();

	void close();

}
