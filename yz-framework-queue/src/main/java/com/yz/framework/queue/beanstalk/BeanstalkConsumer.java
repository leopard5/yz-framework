package com.yz.framework.queue.beanstalk;

import java.util.List;
import java.util.Map;

import com.surftools.BeanstalkClient.BeanstalkException;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.Consumer;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.util.ListUtil;

public class BeanstalkConsumer extends BeanstalkBase implements Consumer {

	private static final Logger logger = Logger.getLogger(BeanstalkConsumer.class);

	private final int MAX_RETRY_TIMES = 3;
	// private final static Logger LOGGER =
	// Logger.getLogger(BeanstalkConsumer.class);
	private String[] tubeNames;

	public String[] getTubeNames() {
		return tubeNames;
	}

	public void setTubeNames(String[] tubeNames) {
		this.tubeNames = tubeNames;
	}

	public BeanstalkConsumer() {

	}

	@Override
	public void start() {
		// TODO 自动生成的方法存根
		super.start(this.tubeNames);
	}

	private ClientConnection currentConnection = null;

	private volatile boolean isLoggedForNoAvailableQueue;

	public JobMessage reserve(int timeoutSeconds) {

		if (clientPool.getAliveCount() == 0) {
			// 避免不停的记录日志
			if (!isLoggedForNoAvailableQueue) {
				isLoggedForNoAvailableQueue = true;
				logger.error("reserve", "没有可用的消息队列，请立即去检查消息队列服务是否停止。");
			}
			return null;
		}
		isLoggedForNoAvailableQueue = false;
		long expire = System.currentTimeMillis() + timeoutSeconds * 1000;
		do {
			List<ClientConnection> activeConnections = ListUtil.copyFrom(clientPool.getAliveConnections());
			for (ClientConnection connection : activeConnections) {
				try {
					com.surftools.BeanstalkClient.Job bJob = connection.getClient().reserve(0);
					if (bJob == null) {
						continue;
					}
					currentConnection = connection;
					return convert(bJob);
				} catch (BeanstalkException e) {
					clientPool.checkConnection(connection);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (System.currentTimeMillis() < expire);
		return null;
	}

	public boolean release(String jobId, long priority, int delaySeconds) {
		if (currentConnection != null) {
			boolean result = currentConnection.release(Long.valueOf(jobId), priority, delaySeconds);
			return result;
		}
		return false;
	}

	public boolean delete(String jobId) {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				boolean result = currentConnection.delete(Long.valueOf(jobId));
				return result;
			} catch (BeanstalkException e) {
				if (!currentConnection.checkIsConnected()) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (currentConnection.isConnectted() && retryTimes < MAX_RETRY_TIMES);
		return false;
	}

	public boolean bury(String jobId, long priority) {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				boolean result = currentConnection.bury(Long.valueOf(jobId), priority);
				return result;
			} catch (BeanstalkException e) {
				e.printStackTrace();
				if (!currentConnection.checkIsConnected()) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} while (currentConnection.isConnectted() && retryTimes < MAX_RETRY_TIMES);
		return false;
	}

	public boolean touch(String jobId) {
		boolean result = currentConnection.touch(Long.valueOf(jobId));
		return result;
	}

	@Override
	public int watch(String tubeName) {
		return currentConnection.getClient().watch(tubeName);
	}

	@Override
	public int ignore(String tubeName) {
		return currentConnection.getClient().ignore(tubeName);
	}

	@Override
	public List<String> listTubesWatched() {
		return currentConnection.getClient().listTubesWatched();
	}

	@Override
	public boolean isConnectted() {
		return currentConnection.isConnectted();
	}

	@Override
	public Map<String, String> statsMessage(String jobId) {
		return currentConnection.getClient().statsJob(Long.valueOf(jobId));
	}

}
