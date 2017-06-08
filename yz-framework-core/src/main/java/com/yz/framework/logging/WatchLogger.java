package com.yz.framework.logging;

import com.alibaba.fastjson.JSON;

public class WatchLogger extends Logger {

	public WatchLogger() {
	}

	public WatchLogger(Class<? extends Object> clazz) {
		super(clazz);
	}

	@Override
	public void info(String methodName, Object... extendsStrings) {
		SysLogEntry logEntry = new SysLogEntry(3, className + "." + methodName, LogFlag.NORMAL.getValue(),
				extendsStrings);
		realLog.info(getLogMessage(logEntry));
	}

	@Override
	public void error(String methodName, Object... extendsStrings) {
		SysLogEntry logEntry = new SysLogEntry(1, className + "." + methodName, LogFlag.ERROR.getValue(),
				extendsStrings);
		realLog.error(getLogMessage(logEntry));
	}

	@Override
	public void error(String methodName, Throwable e) {
		LAST_EXEPTION.set(e);
		SysLogEntry logEntry = new SysLogEntry(1, className + "." + methodName, LogFlag.ERROR.getValue(),
				getDetailMessage(e));
		realLog.error(getLogMessage(logEntry));
	}

	@Override
	public void error(String methodName, String bizTitle, Throwable e) {
		LAST_EXEPTION.set(e);
		SysLogEntry logEntry = new SysLogEntry(1, className + "." + methodName, LogFlag.ERROR.getValue(), bizTitle,
				getDetailMessage(e));
		realLog.error(getLogMessage(logEntry));
	}

	@Override
	public void warn(String methodName, Object... extendsStrings) {
		SysLogEntry logEntry = new SysLogEntry(2, className + "." + methodName, LogFlag.DATA.getValue(), extendsStrings);
		realLog.warn(getLogMessage(logEntry));
	}

	private String getLogMessage(SysLogEntry logEntry) {
		logEntry.setRid(REQUEST_ID_LOCAL.get());
		String logBody = JSON.toJSONString(logEntry);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("yz-" + logEntry.getType() + " " + logBody);
		return stringBuilder.toString();
	}

	@Override
	public void logApiRequest(String serviceName, String api, Object... parameters) {

		SysLogEntry logEntry = new SysLogEntry(
				2,
				api,
				LogFlag.DATA.getValue(),
				parameters);
		realLog.warn(getLogMessage(logEntry));

	}

	@Override
	public void logApiResponse(
			String serviceName,
			String api,
			Integer status,
			String message,
			Object result) {
		SysLogEntry logEntry = null;
		if (isDebugEnabled()) {
			logEntry = new SysLogEntry(
					2,
					api,
					LogFlag.DATA.getValue(),
					status,
					message,
					result);
		}
		else {
			logEntry = new SysLogEntry(
					2,
					api,
					LogFlag.DATA.getValue(),
					status,
					message);
		}
		realLog.warn(getLogMessage(logEntry));

	}

	@Override
	public void doLogEntryIn(
			String invoker,
			String invokerIP,
			String api,
			Object... parameters) {

		SysLogEntry logEntry = new SysLogEntry(
				2,
				api,
				LogFlag.DATA.getValue(),
				parameters);
		realLog.warn(getLogMessage(logEntry));
	}

	@Override
	protected void doLogEntryOut(
			String invoker,
			String invokerIP,
			String api,
			Integer status,
			String message,
			Object result) {
		SysLogEntry logEntry = null;
		if (isDebugEnabled()) {
			logEntry = new SysLogEntry(
					2,
					api,
					LogFlag.DATA.getValue(),
					status, message,
					result);
		}
		else {
			logEntry = new SysLogEntry(
					2,
					api,
					LogFlag.DATA.getValue(),
					status,
					message);
		}

		realLog.warn(getLogMessage(logEntry));

	}

}
