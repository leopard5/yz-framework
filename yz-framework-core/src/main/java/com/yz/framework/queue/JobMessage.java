package com.yz.framework.queue;

import java.util.HashMap;
import java.util.Map;

public class JobMessage {

	public static final String PRODUCE_BY = "produce_by";
	public static final String SEND_RETRY_TIMES = "send_retry_times";
	public static final String CREATED_TIME_MS = "create_time";

	private String messageId;
	private String producedBy;
	private long priority;
	private int delaySeconds;
	private int timeToRun;
	private long timestamp;

	private Map<String, Object> properties;
	private Map<String, String> stats;

	public JobMessage() {
		setProperties(new HashMap<String, Object>());
	}

	public JobMessage(String producedBy, long priority, int delaySeconds, String body) {
		this();
		this.setProducedBy(producedBy);
		this.setPriority(priority);
		this.setDelaySeconds(delaySeconds);
	}

	private String body;

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Map<String, String> getStats() {
		return stats;
	}

	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public int getDelaySeconds() {
		return delaySeconds;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public int getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(int timeToRun) {
		this.timeToRun = timeToRun;
	}

	public String getProducedBy() {
		return producedBy;
	}

	public void setProducedBy(String producedBy) {
		this.producedBy = producedBy;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
