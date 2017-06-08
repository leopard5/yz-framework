package com.yz.framework.security;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectTimeoutException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yz.framework.http.HttpRequestUtil;
import com.yz.framework.http.RequestResult;
import com.yz.framework.util.DateUtil;

public class ApiInvoker {
	private static final Log logger = LogFactory.getLog(ApiInvoker.class);

	private String defautlServiceUrl;
	private ApiRequestDataHandler apiRequestDataHandler;

	public String getDefautlServiceUrl() {
		return defautlServiceUrl;
	}

	public void setDefautlServiceUrl(String defautlServiceUrl) {
		this.defautlServiceUrl = defautlServiceUrl;
	}

	public ApiRequestDataHandler getApiRequestDataHandler() {
		return apiRequestDataHandler;
	}

	public void setApiRequestDataHandler(
			ApiRequestDataHandler apiRequestDataHandler) {
		this.apiRequestDataHandler = apiRequestDataHandler;
	}

	public String invokeApi(String api, String message, String from) throws Exception {
		return invokeApi(defautlServiceUrl, api, message, from);
	}

	public String invokeApi(String serviceUrl, String api, String message,
			String from) throws Exception {
		String data = apiRequestDataHandler.getRequestData(api, message, from);
		return invokePostApi(serviceUrl, data);
	}

	public String invokeJsonApi(String serviceUrl, String from, final Object bizData) throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		String jsonData = JSON.toJSONString(bizData);
		String sign = apiRequestDataHandler.generateSign(jsonData);
		data.put(apiRequestDataHandler.getFromKey(), from);
		data.put(apiRequestDataHandler.getSignKey(), sign);
		data.put(apiRequestDataHandler.getMessageKey(), bizData);
		return doInvokePostApi(serviceUrl, data, true);
	}

	public String invokePostApi(String serviceUrl, String data) throws Exception {
		return doInvokePostApi(serviceUrl, data, false);
	}

	public String invokeJsonPostApi(final String serviceUrl, final Object data) throws Exception {
		return doInvokePostApi(serviceUrl, data, true);
	}

	private String doInvokePostApi(final String serviceUrl, final Object data, final boolean useJSON) throws ConnectTimeoutException, SocketTimeoutException,
			Exception {
		String result = null;
		String startTime = DateUtil.getDate("yyyyMMdd HH:mm:ss SSS");
		try {
			RequestResult requestResult = null;
			if (useJSON) {
				requestResult = HttpRequestUtil.postJSON(serviceUrl, data);
			}
			else {
				requestResult = HttpRequestUtil.post(serviceUrl, (String) data);
			}
			if (requestResult.success()) {
				result = requestResult.getBody();
			}
			else {
				throw new Exception("访问失败，" + requestResult.getReasonPhrase());
			}
		} catch (ConnectTimeoutException ex) {
			logger.error("连接第三方接口超时:", ex);
			throw ex;
		} catch (SocketTimeoutException ex) {
			logger.error("从第三方系统获取响应接口数据超时:", ex);
			throw ex;
		} catch (Exception ex) {
			logger.error("ApiInvoker:ToString:", ex);
			throw ex;
		} finally {
			if (logger.isDebugEnabled()) {
				String endTime = DateUtil.getDate("yyyyMMdd HH:mm:ss SSS");
				logger.debug("start invoke api [" + serviceUrl + "] at Time " + startTime + " ,end at " + endTime);
			}
		}
		return result;
	}

	public String invokeGetApi(String serviceUrl, Map<String, Object> parameters) throws Exception {
		String result = null;
		try {
			RequestResult requestResult = HttpRequestUtil.get(serviceUrl, parameters);
			if (requestResult.success()) {
				result = requestResult.getBody();
			}
			else {
				throw new Exception("访问失败，" + requestResult.getReasonPhrase());
			}
			if (logger.isDebugEnabled()) {
				logger.warn("Invoke In Time:" + DateUtil.getDate("yyyyMMdd HH:mm:ss") + " :END****" + serviceUrl + "***" + parameters.toString() + "***"
						+ result);
			}
		} catch (ConnectTimeoutException ex) {
			logger.error("连接第三方接口超时:" + ex.toString());
			throw ex;
		} catch (SocketTimeoutException ex) {
			logger.error("从第三方系统获取响应接口数据超时:", ex);
			throw ex;
		} catch (Exception ex) {
			logger.error("ApiInvoker:ToString:", ex);
			throw ex;
		}
		return result;
	}
}
