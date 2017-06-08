package com.yz.framework.bizlogging.consumer;

import javax.annotation.Resource;
import com.yz.framework.bizlogging.consumer.dao.ApiInvokeLogDao;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.activemq.MessageListenerBase;

public class ApiInvokeLogListener extends MessageListenerBase {

	private final static Logger LOGGER = Logger.getLogger(ApiInvokeLogListener.class);

	@Resource
	private ApiInvokeLogDao apiInvokeLogDao;

	@Override
	protected void processMessage(JobMessage jobMessage) {
		try {
			apiInvokeLogDao.writeLog(jobMessage);
		} catch (Throwable e) {
			LOGGER.error("onMessage", "持久化Api调用日志失败", e);
		}
	}
}
