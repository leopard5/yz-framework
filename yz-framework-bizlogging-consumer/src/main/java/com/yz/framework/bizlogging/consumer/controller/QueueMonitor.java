package com.yz.framework.bizlogging.consumer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("queue")
public class QueueMonitor {

	// @RequestMapping("index")
	// public String getMonitorStatus() {
	// HttpServletRequest request;
	// return "queue/index";
	// }
	//
	// @RequestMapping("logs")
	// @ResponseBody
	// public String getLogs() {
	// List<BizLogEntry> bizLogEntries = logDao.getLogs();
	// return JSON.toJSONString(bizLogEntries);
	//
	// }
	//
	// @RequestMapping("get")
	// @ResponseBody
	// public String get(long jobId) {
	// BizLogEntry bizLog = logDao.get(jobId);
	// return JSON.toJSONString(bizLog);
	//
	// }
	//
	// @RequestMapping("getClassPath")
	// @ResponseBody
	// public String getClassPath() {
	// // return System.getProperty("java.class.path");
	// // StringBuffer sBuffer = new StringBuffer();
	// // String classpath = System.getProperty("java.class.path");
	// // StringTokenizer st = new StringTokenizer(classpath, ":");
	// // while (st.hasMoreElements()) {
	// // sBuffer.append(st.nextElement() + ";");
	// // }
	// // return sBuffer.toString();
	// // Enumeration<URL> urlsEnumeration;
	// // try {
	// // urlsEnumeration = this.getClassPath();
	// // StringBuffer sBuffer = new StringBuffer();
	// // while (urlsEnumeration.hasMoreElements()) {
	// // URL url = urlsEnumeration.nextElement();
	// // sBuffer.append(url.getPath() + "/");
	// // }
	// // return sBuffer.toString();
	// // } catch (IOException e) {
	// // return e.toString();
	// // }
	//
	// return System.getProperty("java.class.path");
	// }

	// @RequestMapping("/query")
	// @ResponseBody
	// public String queryBizLog(BizLogPageQuery pageReuqest) {
	// QueryResult<BizLogEntry> result = logDao.query(pageReuqest);
	// return JSON.toJSONString(result);
	//
	// }

}
