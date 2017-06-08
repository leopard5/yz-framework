package com.yz.framework.bizlogging.consumer.service;

import com.yz.framework.bizlogging.consumer.dao.ApiInvokeLogDao;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.Consumer;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Worker;

public class ApiInvokeLogWorker extends Worker<JobMessage> {

	private final static Logger logger = Logger.getLogger(ApiInvokeLogWorker.class);
	private Consumer consumer;
	private ApiInvokeLogDao apiInvokeLogDao;

	public ApiInvokeLogWorker(
			Consumer consumer, ApiInvokeLogDao apiInvokeLogDao, JobMessage job) {
		this.consumer = consumer;
		this.apiInvokeLogDao = apiInvokeLogDao;
		this.setJob(job);
	}

	@Override
	public void run() {
		try {
			apiInvokeLogDao.writeLog(this.getJob());
			if (!consumer.delete(this.getJob().getMessageId())) {
				logger.error("run", Thread.currentThread().getName() + "删除Job失败");
			}
		} catch (Exception e) {
			logger.error("run", "持久化日志到数据失败", e);
			consumer.release(this.getJob().getMessageId(), 2, 10);
		}
	}
}
