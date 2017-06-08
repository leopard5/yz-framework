package com.yz.framework.service.consumer.activemq;

import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.activemq.MessageListenerBase;
import com.yz.framework.service.consumer.common.ExecuteTask;

public class ServiceWmsConsumerListener extends MessageListenerBase{
	public static final Logger LOGGER = Logger.getLogger(ServiceWmsConsumerListener.class);

	private ExecuteTask executeTask;
	
	public ExecuteTask getExecuteTask() {
		return executeTask;
	}

	public void setExecuteTask(ExecuteTask executeTask) {
		this.executeTask = executeTask;
	}

	@Override
	protected void processMessage(JobMessage jobMessage) {
		executeTask.doWork(jobMessage, "serviceWmsQueue", "wmsFailedServiceJobQueue");
	}

}
