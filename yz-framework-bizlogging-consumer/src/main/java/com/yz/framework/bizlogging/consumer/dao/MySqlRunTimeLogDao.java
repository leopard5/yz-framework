package com.yz.framework.bizlogging.consumer.dao;

import java.util.Date;
import java.util.HashMap;
import javax.annotation.Resource;

import org.mybatis.spring.SqlSessionTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yz.framework.queue.JobMessage;
import com.yz.framework.util.FormatUtil;

public class MySqlRunTimeLogDao {

	@Resource
	private SqlSessionTemplate sqlSession;

	public static final String TABLE_NAME_PREFIX = "sql_run_log";
	private static String currentTableName = "sql_run_log";

	public void writeLog(JobMessage job) {

		JSONObject jsonObject = JSON.parseObject(new String(job.getBody().toString()));
		long start = jsonObject.getLongValue("start");
		long end = jsonObject.getLongValue("end");
		long used = (end - start);
		jsonObject.put("used", used);
		jsonObject.put("tableName", getTableName());
		sqlSession.insert("MySqlRunTimeLogDao.writeLog", jsonObject);
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
		return sqlSession.update("MySqlRunTimeLogDao.createTable", paraMap);

	}

	private boolean isTableCreated(String tableName) {
		String ss = sqlSession.selectOne("MySqlRunTimeLogDao.selectByTableName", tableName);
		return ss != null && ss.equalsIgnoreCase(tableName);
	}

}
