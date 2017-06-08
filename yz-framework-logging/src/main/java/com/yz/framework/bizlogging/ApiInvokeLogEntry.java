package com.yz.framework.bizlogging;

import java.io.Serializable;

public class ApiInvokeLogEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1777078369646406662L;
	private long id;
	private String jobId;
	private long requestTime;
	private String invoker;
	private String sign;
	private String message;
	private String api;
	private int returnStatus;
	private String returnMessage;
	private String returnData;
	private long returnTime;
	private String invokerIP;
	private String serverIP;
	private String service;
	private String application;

	public long getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public int getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(int returnStatus) {
		this.returnStatus = returnStatus;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

	public long getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(long returnTime) {
		this.returnTime = returnTime;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}


	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getInvokerIP() {
		return invokerIP;
	}

	public void setInvokerIP(String invokerIP) {
		this.invokerIP = invokerIP;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getInvoker() {
		return invoker;
	}

	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

}
