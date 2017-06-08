package com.yz.framework.service.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;

import com.yz.framework.logging.Logger;
import com.yz.framework.queue.Producer;

public class ServiceInvokeWorker implements Runnable, DisposableBean {

	public static final Logger LOGGER = Logger.getLogger(ServiceInvokeWorker.class);
	private int workNum;
	private String consumerBeanName;
	private Producer serviceProducer;
	private int delaySeconds;
	private ExecutorService workerExecutorService;
	private boolean shutdown;

	public Producer getServiceProducer() {
		return serviceProducer;
	}

	public void setServiceProducer(Producer serviceProducer) {
		this.serviceProducer = serviceProducer;
	}

	private int timeToDelay;

	@Override
	public void run() {
		workerExecutorService = Executors.newFixedThreadPool(this.workNum);
		for (int i = 0; i < this.workNum; i++) {
			ServiceTaskExecutor taskExecutor = new ServiceTaskExecutor(this);
			workerExecutorService.execute(taskExecutor);
		}
	}

	public int getTimeToDelay() {
		return timeToDelay;
	}

	public void setTimeToDelay(int timeToDelay) {
		this.timeToDelay = timeToDelay;
	}

	public int getDelaySeconds() {
		return delaySeconds;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public int getWorkNum() {
		return workNum;
	}

	public void setWorkNum(int workNum) {
		this.workNum = workNum;
	}

	public boolean isShutdown() {
		return this.workerExecutorService == null
				|| shutdown
				|| this.workerExecutorService.isShutdown()
				|| this.workerExecutorService.isTerminated();
	}

	@Override
	public void destroy() throws Exception {
		shutdown = true;
		if (workerExecutorService != null) {
			workerExecutorService.shutdown();
		}
	}

	public String getConsumerBeanName() {
		return consumerBeanName;
	}

	public void setConsumerBeanName(String consumerBeanName) {
		this.consumerBeanName = consumerBeanName;
	}
}
