package com.yz.framework.logging;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class NewLogger extends Logger {

	private static final String REQUEST_ID_KEY = "rid";
	private static final String LOG_TIME_KEY = "time";
	private static final String LOG_TYPE_KEY = "type";
	private static final String LOG_LEVEL_KEY = "level";
	private static final String APP_KEY = "app";
	private static final String APP_IP_KEY = "appIP";
	private static final String METHOD_KEY = "md";
	private static final String MESSAGE_KEY = "msg";
	private static final String PARAMETER_KEY = "params";
	private static final String STATUS_KEY = "status";
	private static final String DATA_KEY = "data";
	private static final String INVOKER_KEY = "invoker";
	private static final String INVOKER_IP_KEY = "invokerIP";
	private static final String SERVICE_KEY = "service";

	public NewLogger() {
	}

	public NewLogger(Class<? extends Object> clazz) {
		super(clazz);
	}

	@Override
	public void info(String methodName, Object... extendsStrings) {
		realLog.info(getLogMessage(
				LogType.GENERAL,
				LogFlag.NORMAL,
				methodName,
				extendsStrings));
	}

	@Override
	public void error(String methodName,
			Object... extendsStrings) {

		realLog.error(getLogMessage(
				LogType.GENERAL,
				LogFlag.ERROR,
				methodName,
				extendsStrings));
	}

	@Override
	public void error(String methodName,
			Throwable e) {

		LAST_EXEPTION.set(e);
		realLog.error(getLogMessage(
				LogType.GENERAL,
				LogFlag.ERROR,
				methodName,
				getDetailMessage(e)));
	}

	@Override
	public void error(String methodName, String bizTitle, Throwable e) {
		LAST_EXEPTION.set(e);

		String message = getLogMessage(
				LogType.GENERAL,
				LogFlag.ERROR,
				methodName,
				bizTitle,
				getDetailMessage(e));

		realLog.error(message);
	}

	@Override
	public void warn(String methodName, Object... extendsStrings) {
		realLog.warn(getLogMessage(LogType.GENERAL, LogFlag.ERROR, methodName, extendsStrings));
	}

	private String getLogMessage(
			LogType logType,
			LogFlag logFlag,
			String methodName,
			Object... extensions) {
		Map<String, Object> data = getLogData(methodName, logType, logFlag);
		if (extensions != null && extensions.length > 0) {
			if (extensions.length == 1) {
				data.put(MESSAGE_KEY, extensions[0]);
			}
			else {
				data.put(MESSAGE_KEY, extensions);
			}

		}
		return getLogMessage(data);
	}

	private Map<String, Object> getLogData(String methodName, LogType logType, LogFlag logFlag) {
		Map<String, Object> data = getLogBaseData(logType, logFlag);
		data.put(METHOD_KEY, className + "." + methodName);
		return data;
	}

	private Map<String, Object> getLogBaseData(LogType logType, LogFlag logFlag) {
		Map<String, Object> logData = new HashMap<String, Object>();
		logData.put(REQUEST_ID_KEY, REQUEST_ID_LOCAL.get());
		logData.put(LOG_TIME_KEY, System.currentTimeMillis());
		logData.put(LOG_TYPE_KEY, logType.getValue());
		logData.put(LOG_LEVEL_KEY, logFlag.getValue());
		logData.put(APP_KEY, app);
		logData.put(APP_IP_KEY, appIP);
		return logData;
	}

	@Override
	public void logApiRequest(String serviceName, String api, Object... parameters) {

		Map<String, Object> logData = getLogBaseData(LogType.API_REQUEST, LogFlag.NORMAL);
		logData.put(SERVICE_KEY, serviceName);
		logData.put(METHOD_KEY, api);
		if (parameters != null && parameters.length > 0) {
			if (parameters.length == 1) {
				logData.put(PARAMETER_KEY, parameters[0]);
			}
			else {
				logData.put(PARAMETER_KEY, parameters);
			}

		}
		realLog.info(getLogMessage(logData));

	}

	private String getLogMessage(Map<String, Object> logData) {
		return JSON.toJSONString(logData);
	}

	@Override
	public void logApiResponse(
			String serviceName,
			String api,
			Integer status,
			String message,
			Object result
			) {

		Map<String, Object> logData = getLogBaseData(LogType.API_RETURN, LogFlag.NORMAL);
		logData.put(SERVICE_KEY, serviceName);
		logData.put(METHOD_KEY, api);
		logData.put(STATUS_KEY, status);
		logData.put(MESSAGE_KEY, message);
		if (isDebugEnabled() && result != null) {
			logData.put(DATA_KEY, result);
		}
		realLog.info(getLogMessage(logData));

	}

	@Override
	protected void doLogEntryIn(
			String invoker,
			String invokerIP,
			String api,
			Object... parameters) {

		Map<String, Object> logData = getLogBaseData(LogType.IN, LogFlag.NORMAL);
		logData.put(INVOKER_KEY, invoker);
		logData.put(INVOKER_IP_KEY, invokerIP);
		logData.put(METHOD_KEY, api);
		if (parameters != null && parameters.length > 0) {
			if (parameters.length == 1) {
				logData.put(PARAMETER_KEY, parameters[0]);
			}
			else {
				logData.put(PARAMETER_KEY, parameters);
			}
		}
		realLog.info(getLogMessage(logData));

	}

	@Override
	public void doLogEntryOut(
			String invoker,
			String invokerIP,
			String api,
			Integer status,
			String message,
			Object result) {

		Map<String, Object> logData = getLogBaseData(LogType.OUT, LogFlag.NORMAL);
		logData.put(INVOKER_KEY, invoker);
		logData.put(INVOKER_IP_KEY, invokerIP);
		logData.put(METHOD_KEY, api);
		logData.put(STATUS_KEY, status);
		logData.put(MESSAGE_KEY, message);
		if (isDebugEnabled() && result != null) {
			logData.put(DATA_KEY, result);
		}
		realLog.info(getLogMessage(logData));

	}
}
