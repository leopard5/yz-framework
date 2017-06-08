package com.yz.framework.service.consumer.common;

import java.util.Date;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yz.framework.bizlogging.BizLogger;
import com.yz.framework.config.ConfigManager;
import com.yz.framework.http.HttpRequestUtil;
import com.yz.framework.http.RequestResult;
import com.yz.framework.job.ServiceJob;
import com.yz.framework.job.ServiceTask;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Producer;
import com.yz.framework.util.ApplicationContextUtil;
import com.yz.framework.util.FormatUtil;

public class ExecuteTask {
	public static final Logger LOGGER = Logger.getLogger(ExecuteTask.class);
	
	private Producer serviceProducer;
	private String defaultFailedQueue;
	
	public Producer getServiceProducer() {
		return serviceProducer;
	}
	public void setServiceProducer(Producer serviceProducer) {
		this.serviceProducer = serviceProducer;
	}
	public String getDefaultFailedQueue() {
		return defaultFailedQueue;
	}
	public void setDefaultFailedQueue(String defaultFailedQueue) {
		this.defaultFailedQueue = defaultFailedQueue;
	}
	/**
	 * 自定义失败队列名称
	 * @param jobMessage
	 * @param failedServiceJobQueue
	 */
	public void doWork(JobMessage jobMessage, String destination) {
		doWork(jobMessage, destination, this.getDefaultFailedQueue());
	}
	/**
	 * 运行一个task work
	 * 
	 * @param jobMessage
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年7月22日 上午9:24:16
	 */
	public void doWork(JobMessage jobMessage, String destination, String failedServiceJobQueue) {
		try {
			String json = jobMessage.getBody();
			ServiceJob serviceJob = JSON.parseObject(json, ServiceJob.class);
			for (ServiceTask task : serviceJob.getTaskList()) {
				if (task.isSuccess()) {
					continue;
				}
				executeTask(task);
			}
			boolean hasFailedTask = false;
			if (serviceJob.getTaskList().size() > 0) {
				for (ServiceTask serviceTask : serviceJob.getTaskList()) {
					if (serviceTask.isSuccess()) {
						continue;
					}
					serviceTask.increaseFailedTimes();
					hasFailedTask = true;
				}
				if (hasFailedTask) {
					serviceJob.increaseFailedTimes();
					int delaySecondes = serviceJob.getDelaySeconds();
					jobMessage.setBody(JSON.toJSONString(serviceJob));
					if (delaySecondes > 0) {
						jobMessage.setDelaySeconds(delaySecondes);
						serviceProducer.produce(jobMessage, destination);
					} else {
						jobMessage.setDelaySeconds(0);
						serviceProducer.produce(jobMessage, failedServiceJobQueue);
					}
				}
			}
		} catch (Throwable e) {
			LOGGER.error("doWork", "执行Job发生未知错误", JSON.toJSONString(jobMessage), e);
		}
	}
	
	/**
	 * 具体执行一个任务
	 * 
	 * @param task
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2015年7月22日 上午9:23:48
	 */
	private void executeTask(ServiceTask task) {
		JSONObject jsonObject = null;
		long requestTime = System.currentTimeMillis();
		task.setLastInvokeTime(FormatUtil.formatSSS(new Date(requestTime)));
		int returnStatus = 0;
		String returnMessage = null;
		String returnData = null;
		try {
			String serviceUrl = ConfigManager.getProperty(task.getServiceName());
			if (StringUtils.hasText(serviceUrl)) {
				task.getData().put("from", "service-consumer");
				RequestResult result = HttpRequestUtil.post(serviceUrl, task.getData());
				if (result.success()) {
					jsonObject = JSON.parseObject(result.getBody());
					returnStatus = jsonObject.getInteger("status");
					returnMessage = jsonObject.getString("message");
					returnData = jsonObject.getString("data");
					returnData = isNullOrEmpty(returnData) ? jsonObject.getString("response") : returnData;
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(returnMessage);
					}
				}
				else {
					returnStatus = -1;
					returnMessage = result.getReasonPhrase();
					returnData = "";
					LOGGER.error("executeTask", returnMessage);
				}

			} else {
				returnStatus = -1;
				returnMessage = "没有为" + task.getServiceName() + "配置服务地址";
				returnData = "";
				LOGGER.error("executeTask", returnMessage);
			}

		} catch (Exception e) {
			returnStatus = -1;
			String detailErrorMessage = LOGGER.getDetailMessage(e);
			returnMessage = "调用服务发生系统异常:" + detailErrorMessage;
			returnData = "";
			LOGGER.error("executeTask", "ss", detailErrorMessage);

		} finally {

			task.setReturnData(returnData);
			task.setReturnMessage(returnMessage);
			task.updateReturnStatus(returnStatus);

			logInvoke(
					task.getApi(),
					task.getMessage(),
					task.getFrom(),
					task.getSign(),
					returnStatus,
					returnMessage,
					returnData,
					requestTime);
		}
	}

	private boolean isNullOrEmpty(String returnData) {
		return returnData == null || returnData.trim().length() == 0;
	}

	private void logInvoke(String api, String message, String from, String sign, int status, String returnMessage, String returnData, long requestTime) {
		try {
			BizLogger bizLogger = ApplicationContextUtil.getBean("bizLogger");
			bizLogger.logInvoke(api, message, from, sign, requestTime, status, returnMessage, returnData, "", "", "service-consumer");
		} catch (Exception e) {
			LOGGER.error("logInvoke", "记录调用日志失败", e);
		}
	}
}
