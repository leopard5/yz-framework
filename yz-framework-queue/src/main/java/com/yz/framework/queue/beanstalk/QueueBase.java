package com.yz.framework.queue.beanstalk;

import java.util.Map;

import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClientImpl.ClientImpl;

public class QueueBase {

	protected Client client;
	private String host;
	private int port;
	private boolean isStarted;
	private int maxRetryTimes = 3;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Map<String, String> statsJob(long jobId) {
		return client.statsJob(jobId);
	}

	public Map<String, String> statsTube(String tubeName) {
		return client.statsTube(tubeName);
	}

	public Map<String, String> stats() {
		return client.stats();
	}

	public boolean isConnectted() {
		try {
			Map<String, String> stats = stats();
			return stats != null;
		} catch (Exception e) {
			return false;
		}
	}

	public java.util.List<String> listTubes() {
		return client.listTubes();
	}

	public String listTubeUsed() {
		return client.listTubeUsed();
	}

	private void doStart() {
		client = new ClientImpl(host, port, false);
		setStarted(true);
	}

	public void start() {
		int retriedTimes = 1;
		do {
			try {
				retriedTimes++;
				doStart();
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		} while (!this.isStarted() && retriedTimes < this.getMaxRetryTimes());
	}

	public void stop() {
		setStarted(false);
		if (client != null) {
			client.close();
			client = null;
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	public void setStarted(boolean isStarted) {
		this.isStarted = isStarted;
	}

	public int getMaxRetryTimes() {
		return maxRetryTimes;
	}

	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

}
