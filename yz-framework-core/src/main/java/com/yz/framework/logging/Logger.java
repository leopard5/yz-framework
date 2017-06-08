package com.yz.framework.logging;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.yz.framework.util.StringUtil;

;

public abstract class Logger {

	protected static final RequestIdThreadLocal REQUEST_ID_LOCAL = new RequestIdThreadLocal();
	protected static final ThreadLocal<Throwable> LAST_EXEPTION = new ThreadLocal<Throwable>();
	protected Log realLog = null;
	protected String className = null;
	protected static String loggerImplClass;
	protected static String app;
	protected static String appIP;
	static {

		try {
			appIP = Inet4Address.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			Properties properties = PropertiesLoaderUtils.loadAllProperties("local.properties");
			app = properties.getProperty("appName");
			loggerImplClass = properties.getProperty("logger.impl.class");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Logger() {

	}

	public Logger(Class<? extends Object> clazz) {
		init(clazz);
	}

	private void init(Class<? extends Object> clazz) {
		className = clazz.getName();
		realLog = LogFactory.getLog(clazz);
	}

	public static Logger getLogger(
			Class<? extends Object> clazz) {
		Logger logger = null;

		if (StringUtil.isBlank(loggerImplClass)) {
			logger = new WatchLogger(clazz);
			return logger;
		}

		try {
			logger = (Logger) Logger.class.getClassLoader().loadClass(loggerImplClass).newInstance();
			logger.init(clazz);
		} catch (Exception ex) {
			logger = new WatchLogger(clazz);
		}

		return logger;
	}

	public static Logger getNewLogger(
			Class<? extends Object> clazz) {
		Logger logger = new NewLogger(clazz);
		return logger;
	}

	public abstract void info(String methodName, Object... extentions);

	public abstract void error(String methodName, Object... extentions);

	public abstract void warn(String methodName, Object... extentions);

	public abstract void error(String methodName, Throwable e);

	public abstract void error(String methodName, String bizTitle, Throwable e);

	/**
	 * 记录API调用请求日志
	 * 
	 * @param serviceName
	 *            调用的API所属系统名称
	 * @param api
	 *            调用的API名称
	 * 
	 * @param parameters
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月19日 下午8:00:54
	 */
	public abstract void logApiRequest(
			String serviceName,
			String api,
			Object... parameters);

	/**
	 * 记录API相应日志
	 * 
	 * @param serviceName
	 *            服务名称
	 * @param api
	 *            API名称
	 * @param status
	 *            返回状态码
	 * @param message
	 *            返回消息
	 * @param result
	 *            返回数据，注：只有启动debug模式才记录
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月19日 下午8:06:12
	 */
	public abstract void logApiResponse(
			String serviceName,
			String api,
			Integer status,
			String message,
			Object result);

	/**
	 * 入口日志
	 * 
	 * @param requestId
	 *            请求ID
	 * @param invoker
	 *            调用方名称
	 * @param invokerIP
	 *            调用方IP
	 * @param api
	 *            被调用的API
	 * @param parameters
	 *            调用参数
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月19日 下午2:35:26
	 */
	public void logIn(
			String requestId,
			String invoker,
			String invokerIP,
			String api,
			Object... parameters) {

		REQUEST_ID_LOCAL.set(requestId);
		doLogEntryIn(invoker, invokerIP, api, parameters);
	}

	protected abstract void doLogEntryIn(
			String invoker,
			String invokerIP,
			String api,
			Object... parameters);

	/**
	 * 出口日志
	 * 
	 * @param invoker
	 *            调用方名称
	 * @param invokerIP
	 *            调用方IP
	 * @param api
	 *            被调用的API名称
	 * @param status
	 *            调用返回状态码
	 * @param message
	 *            调用返回消息
	 * @param result
	 *            调用返回结果
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年5月19日 下午2:36:18
	 */
	public void logOut(
			String invoker,
			String invokerIP,
			String api,
			Integer status,
			String message,
			Object result) {

		doLogEntryOut(invoker, invokerIP, api, status, message, result);
		REQUEST_ID_LOCAL.remove();

	}

	protected abstract void doLogEntryOut(String invoker,
			String invokerIP,
			String api,
			Integer status,
			String message,
			Object result);

	public void debug(Object arg0) {
		if (isDebugEnabled()) {
			realLog.debug(arg0);
		}

	}

	public boolean isDebugEnabled() {
		return realLog.isDebugEnabled();
	}

	public String getDetailMessage(Throwable e) {
		StringBuilder sBuilder = new StringBuilder(e.toString());
		StackTraceElement[] stackTraceElements = e.getStackTrace();

		for (StackTraceElement stackTraceElement : stackTraceElements) {
			sBuilder.append(" \n " + stackTraceElement.toString());
		}
		return sBuilder.toString();
	}

	public Throwable getLastException() {
		Throwable e = LAST_EXEPTION.get();
		if (e != null) {
			LAST_EXEPTION.remove();
		}
		return e;
	}
}
