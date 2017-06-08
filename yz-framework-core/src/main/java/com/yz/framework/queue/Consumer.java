package com.yz.framework.queue;

import java.util.List;
import java.util.Map;

public interface Consumer {

	JobMessage reserve(final int timeoutSeconds);

	void close();

	void start();

	boolean isConnectted();

	boolean delete(String messageId);

	boolean release(String messageId, long priority, int delaySeconds);

	boolean bury(String messageId, long priority);

	boolean touch(String messageId);

	int watch(String queueName);

	int ignore(String queueName);

	List<String> listTubesWatched();

	Map<String, String> statsMessage(String messageId);
}
