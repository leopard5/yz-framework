/**
 * yz-framework-core 
 * WebApiInvoker.java 
 * com.yz.framework.security 
 * TODO  
 * @author yazhong.qi
 * @date   2016年8月3日 下午2:42:21 
 * @version   1.0
 */

package com.yz.framework.security;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ConnectTimeoutException;

import com.alibaba.fastjson.JSON;
import com.yz.framework.http.HttpRequestUtil;
import com.yz.framework.http.RequestResult;
import com.yz.framework.util.DateUtil;

/**
 * ClassName:WebApiInvoker <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年8月3日 下午2:42:21 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class WebApiInvoker {
	private static final Log logger = LogFactory.getLog(ApiInvoker.class);

	private String apiKey;
	private String from;

	public static final String FROM_KEY = "from";
	public static final String SIGN_KEY = "signKey";
	public static final String PARAMS_KEY = "params";

	public String invokePostApi(
			final String serviceUrl,
			final Object data) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		String params = JSON.toJSONString(data);
		String sign = DigestUtils.md5Hex(params + getApiKey());
		map.put(FROM_KEY, getFrom());
		map.put(SIGN_KEY, sign);
		map.put(PARAMS_KEY, data);
		return doInvokePostApi(serviceUrl, map);
	}

	private String doInvokePostApi(
			final String serviceUrl,
			final Object data) throws ConnectTimeoutException, SocketTimeoutException,
			Exception {
		String result = null;
		String startTime = DateUtil.getDate("yyyyMMdd HH:mm:ss SSS");
		try {
			RequestResult requestResult = null;
			requestResult = HttpRequestUtil.postJSON(serviceUrl, data);
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

	/**
	 * apiKey.
	 *
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * apiKey.
	 *
	 * @param apiKey
	 *            the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * from.
	 *
	 * @return the from
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * from.
	 *
	 * @param from
	 *            the from to set
	 */
	public void setFrom(String from) {
		this.from = from;
	}
}
