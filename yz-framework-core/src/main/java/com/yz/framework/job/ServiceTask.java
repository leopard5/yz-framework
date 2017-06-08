package com.yz.framework.job;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 代表一个Job里面的任务
 * 
 * @author yazhong.qi
 * @date 2015年7月21日 下午11:18:04
 * @version 1.0
 */
public class ServiceTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4185629805330450948L;

	/**
	 * @Fields success :任务是否成功
	 */
	private boolean success;
	/**
	 * @Fields serviceName : 服务名称
	 */
	private String serviceName;

	/**
	 * @Fields data : 服务调用参数
	 */
	private Map<String, String> data;
	/**
	 * @Fields successStatus : 服务成功的状态标识字符串，中间一逗号分开
	 */
	private String successStatus;
	/**
	 * @Fields failedTimes : 服务调用次数
	 */
	private int failedTimes;

	/**
	 * @Fields returnMessage : 服务调用返回消息
	 */
	private String returnMessage;
	/**
	 * @Fields returnStatus : 服务调用返回的状态
	 */
	private int returnStatus;
	/**
	 * @Fields returnData : 服务调用返回的结果数据
	 */
	private String returnData;
	/**
	 * @Fields lastInvokeTime : 最后一次服务调用时间
	 */
	private String lastInvokeTime;

	public ServiceTask()
	{
		this.data = new HashMap<String, String>();
	}

	public ServiceTask(String serviceName, String api, String message, String sign, String invoker)
	{
		this();
		this.setServiceName(serviceName);
		data.put("api", api);
		data.put("message", message);
		data.put("sign", sign);
		data.put("invoker", invoker);
	}

	public ServiceTask(String serviceName, Map<String, String> data) {
		this();
		this.setServiceName(serviceName);
		this.setData(data);
	}

	public String getSuccessStatus() {
		return successStatus;
	}

	public void setSuccessStatus(String successStatus) {
		this.successStatus = successStatus;
	}

	public int getFailedTimes() {
		return failedTimes;
	}

	public void setFailedTimes(int failedTimes) {
		this.failedTimes = failedTimes;
	}

	public void increaseFailedTimes() {
		this.failedTimes++;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public int getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(int returnStatus) {
		this.returnStatus = returnStatus;
	}

	public void updateReturnStatus(int returnStatus) {
		this.returnStatus = returnStatus;
		setSuccess(false);
		if (isNullOrEmpty(successStatus)) {
			setSuccess((returnStatus == 0 || returnStatus == 10000));
		} else {
			String[] statusS = split(',');
			String returnStatusStr = String.valueOf(statusS);
			for (String status : statusS) {
				if (status.equals(returnStatusStr)) {
					setSuccess(true);
					break;
				}
			}
		}
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

	public String getLastInvokeTime() {
		return lastInvokeTime;
	}

	public void setLastInvokeTime(String lastInvokeTime) {
		this.lastInvokeTime = lastInvokeTime;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	private boolean isNullOrEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	private String[] split(char delimiter) {
		String[] statusS = successStatus.split("\\" + delimiter);
		return statusS;
	}

	@JSONField(serialize = false, deserialize = false)
	public String getApi() {
		String api = data.get("api");
		return isNullOrEmpty(api) ? data.get("func") : api;
	}

	@JSONField(serialize = false, deserialize = false)
	public String getMessage() {
		String message = data.get("message");
		return isNullOrEmpty(message) ? data.get("params") : message;
	}

	@JSONField(serialize = false, deserialize = false)
	public String getFrom() {
		String from = data.get("from");
		return isNullOrEmpty(from) ? data.get("source") : from;
	}

	@JSONField(serialize = false, deserialize = false)
	public String getSign() {
		String sign = data.get("sign");
		return isNullOrEmpty(sign) ? data.get("signKey") : sign;
	}

}
