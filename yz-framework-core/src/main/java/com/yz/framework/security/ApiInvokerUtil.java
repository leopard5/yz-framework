package com.yz.framework.security;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.DigestUtils;

import com.yz.framework.http.HttpRequestUtil;
import com.yz.framework.http.RequestResult;

public abstract class ApiInvokerUtil {

	private final static String API_REQUEST_FORMAT = "api=%s&message=%s&from=%s&sign=%s";

	private final static String LEGACY_API_REQUEST_FORMAT = "func=%s&params=%s&signKey=%s";

	public static String invokePostApi(String serviceUrl, String api, String message, String from, String sign, int timeout)
			throws Exception {
		String data = String.format(API_REQUEST_FORMAT, api, message, from, sign);
		RequestResult requestResult = HttpRequestUtil.post(serviceUrl, data);
		if (requestResult.success()) {
			return requestResult.getBody();
		} else {
			throw new Exception("访问失败，" + requestResult.getReasonPhrase());
		}

	}

	/**
	 * 调用旧系统写的API（PHP）
	 * 
	 * @param serviceUrl
	 * @param api
	 * @param message
	 * @param from
	 * @param sign
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static String invokePostLegacyApi(String serviceUrl, String api, String message, String from, String sign, int timeout)
			throws Exception {
		String data = String.format(LEGACY_API_REQUEST_FORMAT, api, message, sign);
		RequestResult requestResult = HttpRequestUtil.post(serviceUrl, data);
		if (requestResult.success()) {
			return requestResult.getBody();
		} else {
			throw new Exception("访问失败，" + requestResult.getReasonPhrase());
		}

	}

	public static String invokeGetApi(String serviceUrl, String api, String message, String from, String sign, int timeout)
			throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("api", api);
		parameters.put("message", message);
		parameters.put("from", from);
		parameters.put("sign", sign);
		RequestResult requestResult = HttpRequestUtil.get(serviceUrl, parameters);
		if (requestResult.success()) {
			return requestResult.getBody();
		} else {
			throw new Exception("访问失败，" + requestResult.getReasonPhrase());
		}
	}

	/**
	 * 以get方式请求旧系统API(PHP)
	 * 
	 * @param serviceUrl
	 *            服务地址
	 * @param api
	 *            API名称
	 * @param message
	 *            请求参数
	 * @param from
	 *            调用者名称
	 * @param sign
	 *            签名
	 * @param timeout
	 *            调用超时时间 ，毫秒
	 * @return
	 * @throws Exception
	 */
	public static String invokeGetLegacyApi(String serviceUrl, String api, String message, String from, String sign, int timeout)
			throws Exception {

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("func", api);
		parameters.put("params", message);
		parameters.put("signKey", sign);
		RequestResult requestResult = HttpRequestUtil.get(serviceUrl, parameters);
		if (requestResult.success()) {
			return requestResult.getBody();
		} else {
			throw new Exception("访问失败，" + requestResult.getReasonPhrase());
		}
	}

	public static String invokeGet(String serviceUrl) throws Exception {
		RequestResult requestResult = HttpRequestUtil.get(serviceUrl, null);
		if (requestResult.success()) {
			return requestResult.getBody();
		} else {
			throw new Exception("访问失败，" + requestResult.getReasonPhrase());
		}
	}

	public static String generateSign(String message, String md5Key) {

		String md5KeyPre64 = md5Key.substring(0, 64);
		String md5KeyBack64 = md5Key.substring(64);
		String a1 = message + md5KeyPre64;
		a1 = DigestUtils.md5DigestAsHex(a1.getBytes());
		String a2 = a1 + md5KeyBack64;
		String sign = DigestUtils.md5DigestAsHex(a2.getBytes()).toUpperCase();
		return sign;
	}

}
