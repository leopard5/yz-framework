package com.yz.framework.data;

import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.yz.framework.util.ApplicationContextUtil;
import com.yz.framework.util.StringUtil;

@Intercepts({
		@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
		@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }) })
public class SqlTimeInterceptor implements Interceptor {

	private Properties properties;
	private SqlRunTimeLogger logger;
	boolean isInit = false;
	private int minTime = 1000;
	private boolean enableLogging;

	public Object intercept(Invocation invocation) throws Throwable {

		long start = System.currentTimeMillis();
		Object returnValue = invocation.proceed();
		long end = System.currentTimeMillis();
		if ((end - start) > minTime) {
			logger.logSqlRunTime(invocation, start, end);
		}
		return returnValue;
	}

	public Object plugin(Object target) {
		if (enableLogging) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	public void setProperties(Properties properties) {
		if (isInit) {
			return;
		}
		isInit = true;

		String enableLoggingStr = properties.getProperty("enableLogging");
		this.enableLogging = enableLoggingStr.equalsIgnoreCase("true");
		if (!enableLogging) {
			return;
		}
		this.properties = properties;
		logger = ApplicationContextUtil.getBean(SqlRunTimeLogger.class);
		if (logger == null) {
			this.enableLogging = false;
			return;
		}

		logger.setApplication(this.properties.getProperty("application"));
		logger.setDelaySeconds(Integer.valueOf(properties.getProperty("delaySeconds")));
		logger.setTimeToRun(Integer.valueOf(properties.getProperty("timeToRun")));
		logger.setWorkerNum(Integer.valueOf(properties.getProperty("workerNum")));
		String minTimeString = properties.getProperty("minTime");
		if (StringUtil.isNotBlank(minTimeString)) {
			minTime = Integer.valueOf(minTimeString);
		}
		logger.initMethod();
	}

}
