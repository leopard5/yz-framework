package com.yz.framework.bizlogging.consumer.service;

import com.yz.framework.bizlogging.consumer.dao.MySqlRunTimeLogDao;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.Consumer;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Worker;

public class SqlRunTimeLogWorker extends Worker<JobMessage> {

	private final static Logger logger = Logger.getLogger(SqlRunTimeLogWorker.class);
	private Consumer consumer;
	private MySqlRunTimeLogDao dao;

	public SqlRunTimeLogWorker(Consumer consumer, MySqlRunTimeLogDao dao, JobMessage job) {
		this.consumer = consumer;
		this.dao = dao;
		this.setJob(job);
	}

	@Override
	public void run() {
		try {
			dao.writeLog(this.getJob());
			if (!consumer.delete(this.getJob().getMessageId())) {
				logger.error("run", Thread.currentThread().getName() + "删除Job失败");
			}
		} catch (Exception e) {
			logger.error("run", "持久化日志到数据失败", e);
			// consumer.delete(this.getJob().getJobId());
			consumer.release(this.getJob().getMessageId(), 2, 10);
		}
	}
}
