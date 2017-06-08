package com.yz.framework.bizlogging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Producer;

public class BizLogger implements InitializingBean, DisposableBean {

	private static final Logger LOGGER = Logger.getLogger(BizLogger.class);
	private Producer apiInvokeLogProducer;
	private String application;
	private ExecutorService executorService;
	private static final int DEFAULT_WORK_NUM = 100;

	public Producer getApiInvokeLogProducer() {
		return apiInvokeLogProducer;
	}

	public void setApiInvokeLogProducer(Producer apiInvokeLogProducer) {
		this.apiInvokeLogProducer = apiInvokeLogProducer;
	}

	public void logInvoke(final String api,
			final String message,
			final String from,
			final String sign,
			final long requestTime,
			final int returnStatus,
			final String returnMessage,
			final Object returnData,
			final String invokerIP,
			final String serverIP,
			final String service) {
		final long returnTime = System.currentTimeMillis();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				JobMessage JobMessage = buildApiInvokeJobMessage(
						api,
						message,
						from,
						sign,
						requestTime,
						returnTime,
						returnStatus,
						returnMessage,
						returnData,
						invokerIP,
						serverIP,
						service);
				try {
					apiInvokeLogProducer.produce(JobMessage);
				} catch (Throwable e) {
					LOGGER.error("logInvoke", "推送记录Api调用job失败", e);
				}
			}
		});
	}

	public void logInvoke(final String api,
			final String message,
			final String from,
			final String sign,
			final long requestTime,
			final long returnTime,
			final int returnStatus,
			final String returnMessage,
			final Object returnData,
			final String invokerIP,
			final String serverIP,
			final String service) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				JobMessage JobMessage = buildApiInvokeJobMessage(
						api,
						message,
						from,
						sign,
						requestTime,
						returnTime,
						returnStatus,
						returnMessage,
						returnData,
						invokerIP,
						serverIP,
						service);
				try {
					apiInvokeLogProducer.produce(JobMessage);
				} catch (Throwable e) {
					LOGGER.error("logInvoke", "推送记录Api调用job失败", e);
				}
			}
		});
	}

	private JobMessage buildApiInvokeJobMessage(
			String api,
			String message,
			String from,
			String sign,
			long requestTime,
			long returnTime,
			int returnStatus,
			String returnMessage,
			Object returnData,
			String invokerIP,
			String serverIP,
			String service) {
		ApiInvokeLogEntry logEntry = new ApiInvokeLogEntry();
		logEntry.setApi(api);
		logEntry.setMessage(message);
		logEntry.setInvoker(from);
		logEntry.setSign(sign);
		logEntry.setRequestTime(requestTime);
		logEntry.setReturnStatus(returnStatus);
		logEntry.setReturnMessage(returnMessage);
		logEntry.setInvokerIP(invokerIP);
		logEntry.setServerIP(serverIP);
		logEntry.setService(service);
		String strData = getReturnData(returnData);
		logEntry.setReturnData(strData);
		logEntry.setReturnTime(returnTime);
		logEntry.setApplication(this.application);
		JobMessage JobMessage = new JobMessage();
		JobMessage.setBody(JSON.toJSONString(logEntry));
		return JobMessage;
	}

	private String getReturnData(Object returnData) {
		String strData = returnData == null ? "" : JSON.toJSONString(returnData);
		return strData;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	@Override
	public void destroy() throws Exception {

		try {
			executorService.shutdown();
			executorService.awaitTermination(2000, TimeUnit.MICROSECONDS);
		} finally {
			executorService.shutdownNow();
		}
	}

	public synchronized void start() {
		if (executorService == null) {
			executorService = Executors.newFixedThreadPool(DEFAULT_WORK_NUM);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}
}
