package com.yz.framework.queue.beanstalk;

import java.util.Map;

import org.apache.log4j.Logger;

import com.surftools.BeanstalkClient.BeanstalkException;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.QueueManager;

public class BeanstalkQueueManager extends QueueBase implements QueueManager {

	private final static Logger LOGGER = Logger.getLogger(BeanstalkConsumer.class);

	private String tubeName;

	public String getTubeName() {
		return tubeName;
	}

	public void setTubeName(String tubeName) {
		this.tubeName = tubeName;
	}

	@Override
	public JobMessage peek(long jobId) {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				com.surftools.BeanstalkClient.Job job = client.peek(jobId);
				return convert(job);
			} catch (BeanstalkException e) {
				if (!isConnectted()) {
					setStarted(false);
					start();
				}
			}
		} while (this.isStarted() && retryTimes < this.getMaxRetryTimes());
		return null;
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

	@Override
	public JobMessage peekReady() {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				use(tubeName);
				com.surftools.BeanstalkClient.Job job = client.peekReady();
				return convert(job);

			} catch (BeanstalkException e) {
				if (!isConnectted()) {
					setStarted(false);
					start();
				}
			}
		} while (this.isStarted() && retryTimes < this.getMaxRetryTimes());
		return null;

	}

	@Override
	public JobMessage peekDelayed() {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				use(tubeName);
				com.surftools.BeanstalkClient.Job job = client.peekDelayed();
				return convert(job);

			} catch (BeanstalkException e) {
				if (!isConnectted()) {
					setStarted(false);
					start();
				}
			}
		} while (this.isStarted() && retryTimes < this.getMaxRetryTimes());
		return null;
	}

	public JobMessage peekBuried() {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				use(tubeName);
				com.surftools.BeanstalkClient.Job job = client.peekBuried();
				return convert(job);
			} catch (BeanstalkException e) {
				if (!isConnectted()) {
					setStarted(false);
					start();
				}
			}
		} while (this.isStarted() && retryTimes < this.getMaxRetryTimes());
		return null;
	}

	public int kick(int count) {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				use(tubeName);
				int result = client.kick(count);
				return result;

			} catch (BeanstalkException e) {
				if (!isConnectted()) {
					setStarted(false);
					start();
				}
			}
		} while (this.isStarted() && retryTimes < this.getMaxRetryTimes());
		return 0;
	}

	public void use(String tubeName) {
		client.useTube(tubeName);
	}

	@Override
	public boolean delete(long jobId) {
		int retryTimes = 0;
		do {
			try {
				retryTimes++;
				use(tubeName);
				return client.delete(jobId);
			} catch (BeanstalkException e) {
				if (!isConnectted()) {
					setStarted(false);
					start();
				}
			}
		} while (this.isStarted() && retryTimes < this.getMaxRetryTimes());
		return false;
	}

	@Override
	public Map<String, String> statsJobMessage(String JobMessageId) {
		// TODO 自动生成的方法存根
		return null;
	}
}
