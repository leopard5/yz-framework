package com.yz.framework.queue.beanstalk;

import org.springframework.beans.factory.DisposableBean;

import com.yz.framework.queue.JobMessage;

public class BeanstalkBase implements DisposableBean {

	private String hosts;
	protected ClientPool clientPool;

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public void start() {
		clientPool = new ClientPool(hosts);
		clientPool.start();
	}

	public void start(String[] watchTubeNames) {
		clientPool = new ClientPool(hosts, watchTubeNames);
		clientPool.start();
	}

	public void close() {
		if (clientPool != null) {
			clientPool.close();
		}

	}

	protected JobMessage convert(com.surftools.BeanstalkClient.Job job) {
		if (job == null) {
			return null;
		}
		JobMessage queueJob = new JobMessage();
		queueJob.setBody(new String(job.getData()));
		queueJob.setMessageId(String.valueOf(job.getJobId()));
		return queueJob;
	}

	@Override
	public void destroy() throws Exception {
		this.close();
	}
}
