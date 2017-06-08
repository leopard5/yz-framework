package com.yz.framework.bizlogging.consumer.controller;

import java.util.List;

import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yz.framework.bizlogging.ApiInvokeLogEntry;
import com.yz.framework.bizlogging.consumer.dao.ApiInvokeLogDao;
import com.yz.framework.bizlogging.consumer.model.ApiInvokeLogPageQuery;
import com.yz.framework.bizlogging.consumer.model.QueryResult;

@Controller
@RequestMapping("api")
public class ApiInvokeMonitor {

	@Resource
	private ApiInvokeLogDao apiInvokeLogDao;

	@RequestMapping("index")
	public String getMonitorStatus() {

		return "api/index";
	}

	@RequestMapping("logs")
	@ResponseBody
	public String getLogs() {
		List<ApiInvokeLogEntry> apiInvokeLogList = apiInvokeLogDao.getLogs();
		return JSON.toJSONString(apiInvokeLogList);

	}

	@RequestMapping("get")
	@ResponseBody
	public String get(long jobId) {
		ApiInvokeLogEntry invokeLogEntry = apiInvokeLogDao.get(jobId);
		return JSON.toJSONString(invokeLogEntry);

	}

	@RequestMapping("query")
	@ResponseBody
	public String queryBizLog(ApiInvokeLogPageQuery pageReuqest) {
		QueryResult<ApiInvokeLogEntry> result = apiInvokeLogDao.query(pageReuqest);
		return JSON.toJSONString(result);

	}

}
