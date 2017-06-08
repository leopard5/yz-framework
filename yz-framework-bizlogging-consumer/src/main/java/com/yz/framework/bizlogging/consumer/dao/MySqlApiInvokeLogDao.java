package com.yz.framework.bizlogging.consumer.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionTemplate;

import com.alibaba.fastjson.JSON;
import com.yz.framework.bizlogging.ApiInvokeLogEntry;
import com.yz.framework.bizlogging.consumer.model.ApiInvokeLogEntryWithTableName;
import com.yz.framework.bizlogging.consumer.model.ApiInvokeLogPageQuery;
import com.yz.framework.bizlogging.consumer.model.QueryResult;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.util.FormatUtil;

public class MySqlApiInvokeLogDao implements ApiInvokeLogDao {

	@Resource
	private SqlSessionTemplate sqlSession;

	public static final String TABLE_NAME_PREFIX = "api_invoke_log";
	private static String currentTableName = "api_invoke_log";

	public void writeLog(JobMessage job) {
		
		ApiInvokeLogEntryWithTableName apiInvokeLogEntry 
			= JSON.parseObject(job.getBody().toString(), ApiInvokeLogEntryWithTableName.class);
		
		apiInvokeLogEntry.setTableName(getTableName());
		apiInvokeLogEntry.setJobId(job.getMessageId());
		sqlSession.insert("MySqlApiInvokeLogDao.writeLog", apiInvokeLogEntry);
	}

	private String getTableName() {

		String tableName = TABLE_NAME_PREFIX + "_" + FormatUtil.formatYYYYMMDD(new Date());
		if (tableName.equals(currentTableName)) {
			return tableName;
		}
		synchronized (currentTableName) {
			if (tableName.equals(currentTableName)) {
				return tableName;
				
			}
			if (isTableCreated(tableName)) {
				currentTableName = tableName;
				return tableName;
			} else {
				createDailyLogTable(tableName);
				currentTableName = tableName;
				return tableName;
			}

		}
	}

	private int createDailyLogTable(String tableName) {

		HashMap<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("tableName", tableName);
		return sqlSession.update("MySqlApiInvokeLogDao.createTable", paraMap);

	}

	private boolean isTableCreated(String tableName) {
		String ss = sqlSession.selectOne("MySqlApiInvokeLogDao.selectByTableName", tableName);
		return ss != null && ss.equalsIgnoreCase(tableName);
	}

	@Override
	public List<ApiInvokeLogEntry> getLogs() {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public ApiInvokeLogEntry get(long jobId) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public QueryResult<ApiInvokeLogEntry> query(ApiInvokeLogPageQuery pageQuery) {
		return null;
		// QueryResult<ApiInvokeLogEntry> result = new
		// QueryResult<ApiInvokeLogEntry>();
		// Criteria criteria = new Criteria();
		// if (StringUtils.hasText(pageQuery.getApi())) {
		// criteria = criteria.and("api").regex(pageQuery.getApi(), "m");
		// }
		// Query query = Query.query(criteria);
		// long total = mongoTemplate.count(query, BizLogEntry.class,
		// "ApiInvokeLog");
		// result.setTotal(total);
		// query = query.with(new Sort(Direction.DESC,
		// "jobId")).skip(pageQuery.getSkip()).limit(pageQuery.getRows());
		// result.setRows(mongoTemplate.find(query, ApiInvokeLogEntry.class,
		// "ApiInvokeLog"));
		// return result;
	}

}
