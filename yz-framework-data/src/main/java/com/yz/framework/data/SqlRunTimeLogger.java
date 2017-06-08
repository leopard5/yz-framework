package com.yz.framework.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.springframework.beans.factory.DisposableBean;

import com.alibaba.fastjson.JSONObject;
import com.yz.framework.logging.Logger;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.queue.Producer;

/**
 * 记录sql运行时间
 * 
 * @author yazhong.qi
 *
 */
public class SqlRunTimeLogger implements DisposableBean {

	private static final Logger LOGGER = Logger.getLogger(SqlRunTimeLogger.class);
	private Producer sqlRunLogProducer;
	private ExecutorService executorService;
	private int delaySeconds;
	private int timeToRun;
	private int workerNum = 10;
	private String application;

	public int getDelaySeconds() {
		return delaySeconds;
	}

	public void setDelaySeconds(int delaySeconds) {
		this.delaySeconds = delaySeconds;
	}

	public Producer getSqlRunLogProducer() {
		return sqlRunLogProducer;
	}

	public void setSqlRunLogProducer(Producer sqlRunLogProducer) {
		this.sqlRunLogProducer = sqlRunLogProducer;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public int getTimeToRun() {
		return timeToRun;
	}

	public void setTimeToRun(int timeToRun) {
		this.timeToRun = timeToRun;
	}

	public void logSqlRunTime(final Invocation invocation, final long start, final long end) {
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Object[] args = invocation.getArgs();
					MappedStatement mappedStatement = (MappedStatement) args[0];
					String sqlId = mappedStatement.getId();

					String sql = getSql(args, mappedStatement);
					JobMessage job = new JobMessage();
					job.setDelaySeconds(delaySeconds);
					job.setTimeToRun(timeToRun);
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("application", application);
					jsonObject.put("sqlId", sqlId);
					jsonObject.put("sql", sql.replaceAll("[\\s]+", " "));
					jsonObject.put("start", start);
					jsonObject.put("end", end);
					job.setBody(jsonObject.toJSONString());
					String jobId = sqlRunLogProducer.produce(job);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("成功提交Job:" + jobId);
					}
				} catch (Throwable e) {
					LOGGER.error("logSqlRunTime", "提交job失败 ", e);
				}
			}

			private String getSql(Object[] args, MappedStatement mappedStatement) {
				try {
					BoundSql boundSql = null;
					if (args.length > 1) {
						Object parameterObject = args[1];
						boundSql = mappedStatement.getBoundSql(parameterObject);
					} else {
						boundSql = mappedStatement.getBoundSql(null);
					}
					String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(sql);
					}
					return sql;
				} catch (Exception e) {
					LOGGER.warn("logSqlRunTime", "获取" + mappedStatement.getId() + "失败", LOGGER.getDetailMessage(e));
				}
				return "";

			}
		});
	}

	public void initMethod() {
		try {
			executorService = Executors.newFixedThreadPool(workerNum);
			sqlRunLogProducer.start();
		} catch (Exception e) {
			LOGGER.error("initMethod", "cannot connect to beanstalk", e);
		}
	}

	public int getWorkerNum() {
		return workerNum;
	}

	public void setWorkerNum(int workerNum) {
		this.workerNum = workerNum;
	}

	@Override
	public void destroy() throws Exception {
		if (executorService != null) {
			LOGGER.info("shutdown sql time record thread pool gracefully ");
			executorService.shutdownNow();
		}
	}
}
