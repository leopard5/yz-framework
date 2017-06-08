package com.yz.framework.queue.beanstalk;

import javax.transaction.NotSupportedException;

import com.surftools.BeanstalkClient.BeanstalkException;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Producer;

public class BeanstalkProducer extends BeanstalkBase implements Producer {
	private static final Logger logger = Logger.getLogger(BeanstalkProducer.class);

	private int minTimeToRun = 100;

	public int getMinTimeToRun() {
		return minTimeToRun;
	}

	public void setMinTimeToRun(int minTimeToRun) {
		this.minTimeToRun = minTimeToRun;
	}

	private String tubeName;

	public String getTubeName() {
		return tubeName;
	}

	public void setTubeName(String tubeName) {
		this.tubeName = tubeName;
	}

	public BeanstalkProducer() {

	}

	public String produce(final JobMessage job) {
		return doProduce(job, this.tubeName);
	}

	private String doProduce(final JobMessage job, final String tube) {
		int ttr = job.getTimeToRun();
		ttr = ttr > minTimeToRun ? ttr : minTimeToRun;
		job.setTimeToRun(ttr);
		if (clientPool == null) {
			start();
		}
		if (!clientPool.isStartted()) {
			clientPool.start();
		}
		int retryTimes = 0;
		do {
			ClientConnection connection = null;
			try {
				retryTimes++;
				connection = clientPool.getConnection();
				if (connection == null) {
					logger.error("produce", "no available queue for procude job");
				}
				connection.getClient().useTube(tube);
				long jobId = connection.getClient().put(job.getPriority(), job.getDelaySeconds(), job.getTimeToRun(), job.getBody().getBytes());
				job.setMessageId(String.valueOf(jobId));
				return job.getMessageId();

			} catch (BeanstalkException e) {
				logger.error("produce", e);
				clientPool.checkConnection(connection);
			} catch (Exception e) {
				logger.error("produce", e);
			}
		} while (retryTimes < clientPool.getAliveCount());
		logger.warn("produce", "produce job failed, reach the max retry times + " + clientPool.getAliveCount());
		return null;
	}

	@Override
	public String produce(JobMessage message, String queueName) throws Throwable {
		return doProduce(message, queueName);
	}

	@Override
	public String publish(JobMessage message, String topicName) throws Throwable {
		throw new NotSupportedException();
	}

	@Override
	public String publish(JobMessage jobMessage) throws Throwable {
		throw new NotSupportedException();
	}

}
