package com.yz.framework.service.consumer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yz.framework.bizlogging.BizLogger;
import com.yz.framework.config.ConfigManager;
import com.yz.framework.job.ServiceJob;
import com.yz.framework.job.ServiceTask;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.Consumer;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.security.ApiInvokerUtil;
import com.yz.framework.util.ApplicationContextUtil;

public class ServiceTaskExecutor implements Runnable {

	public static final Logger LOGGER = Logger.getLogger(ServiceTaskExecutor.class);
	private ServiceInvokeWorker worker;
	private Consumer serviceConsumer;

	public ServiceTaskExecutor(ServiceInvokeWorker worker) {
		this.worker = worker;
	}

	@Override
	public void run() {
		serviceConsumer = ApplicationContextUtil.getBean(worker.getConsumerBeanName());
		serviceConsumer.start();
		while (!worker.isShutdown()) {
			JobMessage job = null;
			try {
				job = serviceConsumer.reserve(10);
				if (job == null) {
					continue;
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("get job " + job.getMessageId());
				}
				doWork(job);
			} catch (Exception e) {
				LOGGER.error("start", e);
				if (job != null) {
					if (serviceConsumer.bury(job.getMessageId(), 10000)) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("bury job " + job.getMessageId());
						}
					}
				}
			}
		}
	}

	private void doWork(JobMessage job) {
		try {
			String json = (String) job.getBody();
			ServiceJob serviceJob = JSON.parseObject(json, ServiceJob.class);
			for (ServiceTask task : serviceJob.getTaskList()) {
				executeTask(task);
			}
			removeSuccessTask(serviceJob);
			if (serviceJob.getTaskList().size() == 0) {
				delete(job.getMessageId());
			} else {
				for (ServiceTask serviceTask : serviceJob.getTaskList()) {
					serviceTask.setFailedTimes(serviceTask.getFailedTimes() + 1);
				}
				// 如果存在producer表示需要放在下一个queue里面
				if (worker.getServiceProducer() != null) {
					String serviceJobJson = JSON.toJSONString(serviceJob);
					JobMessage newJob = new JobMessage();
					newJob.setBody(serviceJobJson);
					newJob.setTimeToRun(job.getTimeToRun());
					newJob.setDelaySeconds(worker.getDelaySeconds());
					if (worker.getServiceProducer().produce(newJob) != null) {// 如果成功放入下一个队列
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("produce job " + newJob.getMessageId());
						}
						delete(job.getMessageId());
					} else {
						LOGGER.error("run", "把Job放入下一个队列失败", JSON.toJSONString(job));
					}
				} else {
					if (serviceConsumer.bury(job.getMessageId(), 10000)) {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("bury job " + job.getMessageId() + " ok");
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("bury job " + job.getMessageId() + " failed");
						}
					}
				}
			}

		} catch (Throwable e) {
			delete(job.getMessageId());
			LOGGER.error("run", "执行Job发生未知错误", JSON.toJSONString(job), e);
		}
	}

	private void removeSuccessTask(ServiceJob serviceJob) {
		List<ServiceTask> unSucessTaskList = new ArrayList<ServiceTask>();
		for (ServiceTask task : serviceJob.getTaskList()) {
			if (task.isSuccess()) {
				continue;
			}
			unSucessTaskList.add(task);
		}
		serviceJob.setTaskList(unSucessTaskList);
	}

	private void executeTask(ServiceTask task) {
		JSONObject jsonObject = null;
		long requestTime = System.currentTimeMillis();
		int returnStatus = 0;
		String returnMessage = null;
		String returnData = null;
		try {
			String serviceUrl = ConfigManager.getProperty(task.getServiceName());
			if (StringUtils.hasText(serviceUrl)) {

				String message = ApiInvokerUtil.invokePostApi(serviceUrl, task.getApi(), task.getMessage(), task.getFrom(), task.getSign(), 10 * 1000);
				jsonObject = JSON.parseObject(message);
				returnStatus = jsonObject.getInteger("status");
				returnData = jsonObject.getString("data");
				returnMessage = jsonObject.getString("message");
				task.setReturnData(returnData);
				task.setReturnMessage(returnMessage);
				task.setReturnStatus(returnStatus);

			} else {
				returnStatus = -1;
				returnMessage = "没有为" + task.getServiceName() + "配置服务地址";
				task.setReturnData(returnData);
				task.setReturnMessage(returnMessage);
				task.setReturnStatus(returnStatus);
				LOGGER.error("executeTask", returnMessage);
			}

		} catch (Exception e) {
			returnStatus = -1;
			String detailErrorMessage = LOGGER.getDetailMessage(e);
			returnMessage = "调用服务发生系统异常:" + detailErrorMessage;
			task.setReturnData(returnData);
			task.setReturnMessage(returnMessage);
			task.setReturnStatus(returnStatus);
			LOGGER.error("executeTask", returnMessage, JSON.toJSONString(task), detailErrorMessage);

		} finally {
			logInvoke(task.getApi(), task.getMessage(), task.getFrom(), task.getSign(), returnStatus, returnMessage, returnData, requestTime);
		}
	}

	@Resource
	private BizLogger bizLogger;

	private void logInvoke(String api, String message, String from, String sign, int status, String returnMessage, String returnData, long requestTime) {

		bizLogger.logInvoke(api, message, from, sign, requestTime, status, returnMessage, returnData, "", "", "service-consumer");

	}

	public boolean delete(String jobId) {
		boolean isDeleted = false;
		isDeleted = serviceConsumer.delete(jobId);
		if (isDeleted) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("delete job ok,jobid =" + jobId);
			}
		} else {
			LOGGER.debug("delete job failed,jobid =" + jobId);
		}

		return isDeleted;
	}

}
