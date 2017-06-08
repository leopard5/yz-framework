package com.yz.framework.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.yz.framework.util.ListUtil;

public class ServiceJob implements Serializable {

	public ServiceJob()
	{
		this.setTaskList(new ArrayList<ServiceTask>());
		this.setInvokeMode(ServiceJob.INVOKE_MODE_SEQ);
	}

	public ServiceJob(
			int invokeMode,
			String serviceName,
			String api,
			String message,
			String sign,
			String from,
			String retryDelpayTimes)
	{

		this();
		this.setInvokeMode(invokeMode);
		if (StringUtils.hasText(retryDelpayTimes)) {
			this.setRedelivoryDelays(ListUtil.fromString(retryDelpayTimes));
		}
		ServiceTask serviceTask = new ServiceTask(serviceName, api, message, sign, from);
		this.getTaskList().add(serviceTask);
	}

	public ServiceJob(
			int invokeMode,
			String serviceName,
			Map<String, String> data,
			String retryDelpayTimes)
	{

		this();
		this.setInvokeMode(invokeMode);
		if (StringUtils.hasText(retryDelpayTimes)) {
			this.setRedelivoryDelays(ListUtil.fromString(retryDelpayTimes));
		}
		ServiceTask serviceTask = new ServiceTask(serviceName, data);
		this.getTaskList().add(serviceTask);
	}

	/**
	 * 顺序调用模式，前面的Task没有成功，不能执行后的Task
	 */
	public static final int INVOKE_MODE_SEQ = 0;
	/**
	 * 并发调用模式
	 */
	public static final int INVOKE_MODE_CON = 1;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1355868862789277522L;

	/**
	 * 
	 */
	private int invokeMode;

	public int getInvokeMode() {
		return invokeMode;
	}

	public void setInvokeMode(int invokeMode) {
		this.invokeMode = invokeMode;
	}

	private int failedTimes;

	private List<String> redelivoryDelays;

	public int getFailedTimes() {
		return failedTimes;
	}

	public void setFailedTimes(int failedTimes) {
		this.failedTimes = failedTimes;
	}

	public void increaseFailedTimes() {
		this.failedTimes++;
	}

	private List<ServiceTask> taskList;

	public List<ServiceTask> getTaskList() {
		return taskList;
	}

	public void setTaskList(List<ServiceTask> taskList) {
		this.taskList = taskList;
	}

	public List<String> getRedelivoryDelays() {
		return redelivoryDelays;
	}

	public void setRedelivoryDelays(List<String> redelivoryDelays) {
		this.redelivoryDelays = redelivoryDelays;
	}

	final static Pattern ssPattern = Pattern.compile("^\\d+$");

	public int getDelaySeconds() {
		if (this.redelivoryDelays == null || this.failedTimes < 1 || this.failedTimes > this.redelivoryDelays.size()) {
			return -1;
		}
		String delayString = this.redelivoryDelays.get(this.failedTimes - 1);
		char timeUnit = delayString.charAt(delayString.length() - 1);
		String timeNumStr = delayString.substring(0, delayString.length() - 1);
		if (ssPattern.matcher(timeNumStr).matches()) {
			int timeNum = Integer.valueOf(timeNumStr).intValue();
			if (timeUnit == 's' || timeUnit == 'S') {
				return timeNum;
			}
			if (timeUnit == 'm') {
				return timeNum * 60;
			}
			if (timeUnit == 'h' || timeUnit == 'H') {
				return timeNum * 60 * 60;
			}
			if (timeUnit == 'd' || timeUnit == 'D') {
				return timeNum * 60 * 60 * 24;
			}
			if (timeUnit == 'M') {
				return timeNum * 60 * 60 * 24 * 30;
			}
			if (timeUnit == 'y' || timeUnit == 'Y') {
				return timeNum * 60 * 60 * 24 * 30 * 12;
			}
		}
		return -1;
	}

}
