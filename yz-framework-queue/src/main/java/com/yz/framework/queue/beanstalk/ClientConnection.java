package com.yz.framework.queue.beanstalk;

import java.util.Map;

import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClientImpl.ClientImpl;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;

public class ClientConnection {
	private static final Logger logger = Logger.getLogger(ClientConnection.class);
	// private final int MAX_RETRY_TIMES = 3;
	public static final String DEFAULT_TUBE_NAME = "default";
	protected Client client;
	private boolean connectted = false;
	private String host;
	private String[] tubeNames;

	public String[] getTubeNames() {
		return tubeNames;
	}

	public void setTubeNames(String[] tubeNames) {
		this.tubeNames = tubeNames;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	private int port;

	public ClientConnection(String host, int port) {
		this(host, port, null);
	}

	public ClientConnection(String host, int port, String[] tubeNames) {
		this.host = host;
		this.port = port;
		this.tubeNames = tubeNames;
	}

	public Client getClient() {
		return client;
	}

	public boolean isConnectted() {
		return connectted;
	}

	public void setConnectted(boolean connectted) {
		this.connectted = connectted;
	}

	public void close() {
		connectted = false;
		if (null != client) {
			client.close();
			client = null;
		}
	}

	public boolean checkIsConnected() {
		try {
			Map<String, String> stats = stats();
			return stats != null;
		} catch (Exception e) {
			return false;
		}
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

	public java.util.List<String> listTubes() {
		return client.listTubes();
	}

	public String listTubeUsed() {
		return client.listTubeUsed();
	}

	protected JobMessage convert(com.surftools.BeanstalkClient.Job job) {
		if (job == null) {
			return null;
		}
		JobMessage queueJob = new JobMessage();
		statsJob(job.getJobId());
		queueJob.setBody(new String(job.getData()));
		queueJob.setMessageId(String.valueOf(job.getJobId()));
		return queueJob;
	}

	private volatile boolean isLoggedForConnectFailed;

	public boolean connect() {
		try {
			client = new ClientImpl(host, port, false);
			if (tubeNames != null) {
				for (String tubeName : tubeNames) {
					watch(tubeName);
				}
				client.ignore(DEFAULT_TUBE_NAME);
			}
			connectted = true;
			isLoggedForConnectFailed = false;
		} catch (Exception e) {
			// 避免重复发送日志
			if (!isLoggedForConnectFailed) {
				isLoggedForConnectFailed = true;
				logger.error("connect", "连接队列服务器失败，请检查队列服务器," + host + ":" + port, e);
			}
			connectted = false;
		}
		return connectted;
	}

	public void watch(String tubeName) {
		client.watch(tubeName);
	}

	public boolean release(long jobId, long priority, int delaySeconds) {
		return client.release(jobId, priority, delaySeconds);
	}

	public boolean delete(long jobId) {
		return client.delete(jobId);
	}

	public boolean bury(long jobId, long priority) {
		return client.bury(jobId, priority);
	}

	public boolean touch(long jobId) {
		return client.touch(jobId);
	}

}
