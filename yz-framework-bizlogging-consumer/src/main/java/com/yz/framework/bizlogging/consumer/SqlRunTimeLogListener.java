package com.yz.framework.bizlogging.consumer;

import javax.annotation.Resource;
import com.yz.framework.bizlogging.consumer.dao.MySqlRunTimeLogDao;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.activemq.MessageListenerBase;

public class SqlRunTimeLogListener extends MessageListenerBase {

	private final static Logger LOGGER = Logger.getLogger(SqlRunTimeLogListener.class);

	@Resource
	private MySqlRunTimeLogDao mySqlRunTimeLogDao;

	@Override
	protected void processMessage(JobMessage jobMessage) {
		try {
			mySqlRunTimeLogDao.writeLog(jobMessage);
		} catch (Throwable e) {
			LOGGER.error("onMessage", "持久化Api调用日志失败", e);
		}
	}
}
