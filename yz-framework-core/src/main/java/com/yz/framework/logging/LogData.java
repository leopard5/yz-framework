/**
 * yz-framework-core 
 * LogData.java 
 * com.yz.framework.logging 
 * TODO  
 * @author yazhong.qi
 * @date   2016年5月19日 下午8:52:33 
 * @version   1.0
 */

package com.yz.framework.logging;

/**
 * ClassName:LogData <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月19日 下午8:52:33 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class LogData {

	private String rid;
	private long time;
	private byte level;
	private byte type;

	private String app;
	private String appId;
	private String md;
	private String msg;

	private Object params;
	private Integer status;
	private Object data;
	private String invoker;
	private String invokerIP;
	private String service;

	public LogData() {
		this.time = System.currentTimeMillis();
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public byte getLevel() {
		return level;
	}

	public void setLevel(byte level) {
		this.level = level;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getInvoker() {
		return invoker;
	}

	public void setInvoker(String invoker) {
		this.invoker = invoker;
	}

	public String getInvokerIP() {
		return invokerIP;
	}

	public void setInvokerIP(String invokerIP) {
		this.invokerIP = invokerIP;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

}
