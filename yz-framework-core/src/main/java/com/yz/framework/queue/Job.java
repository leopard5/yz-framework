package com.yz.framework.queue;

import java.util.Map;

public class Job {

	private long jobId;
	private long priority;
	private int delaySeconds;
	private int timeToRun;

	private byte[] data;

	private Map<String, String> stats;

	public long getJobId() {
		return jobId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
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

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Map<String, String> getStats() {
		return stats;
	}

	public void setStats(Map<String, String> stats) {
		this.stats = stats;
	}
 }
