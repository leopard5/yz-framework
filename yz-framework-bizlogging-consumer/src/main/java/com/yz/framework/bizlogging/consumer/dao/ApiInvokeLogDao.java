package com.yz.framework.bizlogging.consumer.dao;

import java.util.List;

import com.yz.framework.bizlogging.ApiInvokeLogEntry;
import com.yz.framework.bizlogging.consumer.model.ApiInvokeLogPageQuery;
import com.yz.framework.bizlogging.consumer.model.QueryResult;
import com.yz.framework.queue.JobMessage;

public interface ApiInvokeLogDao {

	void writeLog(JobMessage job);

	List<ApiInvokeLogEntry> getLogs();

	ApiInvokeLogEntry get(long jobId);

	QueryResult<ApiInvokeLogEntry> query(ApiInvokeLogPageQuery pageReuqest);
}
