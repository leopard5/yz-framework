package com.yz.framework.service.consumer;

import java.util.List;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import com.yz.framework.logging.Logger;

public class ConsumerService implements ApplicationListener<ApplicationEvent> {

	private final static Logger logger = Logger.getLogger(ConsumerService.class);
	private List<ServiceInvokeWorker> workers;
	public volatile boolean shutdown;

	public void init() {
		logger.info("init", "start service workers,worker counts is " + workers.size());
		for (ServiceInvokeWorker worker : workers) {
			worker.run();
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextStoppedEvent) {
			shutdown = true;
		}
		if (event instanceof ContextRefreshedEvent) {
			shutdown = false;
		}
	}

	public List<ServiceInvokeWorker> getWorkers() {
		return workers;
	}

	public void setWorkers(List<ServiceInvokeWorker> workers) {
		this.workers = workers;
	}
}
