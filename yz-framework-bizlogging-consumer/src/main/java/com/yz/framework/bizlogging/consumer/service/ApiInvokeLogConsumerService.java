package com.yz.framework.bizlogging.consumer.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import com.yz.framework.bizlogging.consumer.dao.ApiInvokeLogDao;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.Consumer;
import com.yz.framework.queue.JobMessage;

public class ApiInvokeLogConsumerService implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {

	private final static Logger logger = Logger.getLogger(ApiInvokeLogConsumerService.class);

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Resource
	private ApiInvokeLogDao apiInvokeLogDao;

	private volatile boolean done;
	private int workerNum = 20;
	private int timeoutSeconds;
	private ExecutorService workerExecutorService;

	public void init() {
		workerExecutorService = Executors.newFixedThreadPool(workerNum);
		start();
	}

	public int getWorkerNum() {
		return workerNum;
	}

	public void setWorkerNum(int workerNum) {
		this.workerNum = workerNum;
	}

	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}

	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}

	public void start() {
		done = false;
		for (int i = 0; i < workerNum; i++) {
			Thread serviceThread = new Thread(new Runnable() {
				@Override
				public void run() {
					Consumer apiInvokeLogConsumer = applicationContext.getBean("apiInvokeLogConsumer", Consumer.class);
					apiInvokeLogConsumer.start();
					logger.info("start", Thread.currentThread().getName() + " started");
					while (!done) {
						try {
							JobMessage job = apiInvokeLogConsumer.reserve(timeoutSeconds);
							if (job == null) {
								continue;
							}
							logger.debug(Thread.currentThread().getName() + " get job " + job.getMessageId());
							ApiInvokeLogWorker worker = new ApiInvokeLogWorker(apiInvokeLogConsumer, apiInvokeLogDao, job);
							worker.run();
						} catch (Throwable e) {
							logger.error("start", e);
						}
					}
					apiInvokeLogConsumer.close();
					logger.info("start", Thread.currentThread().getName() + " is stopped");
				}
			});
			workerExecutorService.execute(serviceThread);
		}
	}

	public void stop() {
		done = true;
		workerExecutorService.shutdown();
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent) {
			stop();
		}
	}
}
