package com.yz.framework.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yz.framework.util.MapUtil;

/**
 * 用于发送http请求，包括post和get方式.
 * 
 * @author yazhong.qi
 *
 */
public abstract class HttpRequestUtil {

	/**
	 * 默认编码名称.
	 */
	public static final String DEFAULT_CHARSET_STRING = "UTF-8";

	/**
	 * 默认编码.
	 */
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

	/**
	 * 默认连接时间
	 */
	public static final int DEFAULT_CONNECTION_TIMEOUT = 100000000;

	/**
	 * 默认socket连接超时时间
	 */
	public static final int DEFAULT_SOCKET_TIMEOUT = 100000000;

	/**
	 * 根据response构建RequestResult对象.
	 * 
	 * @param response
	 * @return 返回一个{@link RequestResult}对象.
	 * @throws IOException
	 */
	private static RequestResult buildRequestResult(final HttpResponse response) throws IOException {
		HttpEntity enityEntity = response.getEntity();
		byte[] bytes = EntityUtils.toByteArray(enityEntity);
		RequestResult requestResult = new RequestResult();
		String result = new String(bytes, DEFAULT_CHARSET);
		requestResult.setStatus(response.getStatusLine().getStatusCode());
		requestResult.setReasonPhrase(response.getStatusLine().getReasonPhrase());
		requestResult.setBody(result);
		return requestResult;
	}

	/**
	 * 执行一个http get 请求.
	 * 
	 * @param serviceUrl
	 * @param parameters
	 * @return 返回一个{@link RequestResult}对象.
	 * @throws Exception
	 */
	public static RequestResult get(final String serviceUrl, final Map<String, Object> parameters) throws Exception {
		HttpClient httpclient = new DefaultHttpClient();
		String url = serviceUrl;
		if (parameters != null && parameters.size() > 0) {
			if (serviceUrl.endsWith("?")) {
				url = url + MapUtil.map2QueryString(parameters);
			}
			else {
				url = url + "?" + MapUtil.map2QueryString(parameters);
			}
		}
		HttpGet request = new HttpGet(url);
		request.setHeader("Accept-Charset", DEFAULT_CHARSET_STRING);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
		HttpResponse response = httpclient.execute(request);
		RequestResult requestResult = buildRequestResult(response);
		return requestResult;
	}

	/**
	 * 执行一个http post 请求.
	 * 
	 * @param serviceUrl
	 * @param data
	 * @return 返回一个{@link RequestResult}对象.
	 * @throws Exception
	 */
	public static RequestResult postJSON(final String serviceUrl, final Object data) throws Exception {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(serviceUrl);
		request.setHeader("Accept-Charset", DEFAULT_CHARSET_STRING);
		ContentType contentType = ContentType.create("application/json", DEFAULT_CHARSET);
		String jsonData = null;
		if (data instanceof String) {
			jsonData = (String) data;
		}
		else if (data instanceof JSONObject) {
			jsonData = ((JSONObject) data).toJSONString();
		}
		else {
			jsonData = JSON.toJSONString(data);
		}
		HttpEntity entryEntity = new StringEntity(jsonData, contentType);
		request.setEntity(entryEntity);
		HttpResponse response;
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
		response = httpclient.execute(request);
		RequestResult requestResult = buildRequestResult(response);
		return requestResult;
	}

	public static RequestResult post(final String serviceUrl, final String data) throws Exception {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(serviceUrl);
		request.setHeader("Accept-Charset", DEFAULT_CHARSET_STRING);
		ContentType contentType = ContentType.create(
				"application/x-www-form-urlencoded", DEFAULT_CHARSET);
		HttpEntity entryEntity = new StringEntity(data, contentType);
		request.setEntity(entryEntity);
		HttpResponse response;
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
		response = httpclient.execute(request);
		RequestResult requestResult = buildRequestResult(response);
		return requestResult;
	}

	public static RequestResult post(final String serviceUrl, final Map<String, ?> data) throws Exception {

		StringBuilder sb = new StringBuilder();
		if (data != null && !data.isEmpty()) {
			Set<String> keys = data.keySet();
			for (String key : keys) {
				sb.append(key + "=" + data.get(key) + "&");
			}
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return post(serviceUrl, sb.toString());
	}

	public static RequestResult post2(final String serviceUrl, final Map<String, Object> data) throws Exception {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost request = new HttpPost(serviceUrl);
		request.setHeader("Accept-Charset", DEFAULT_CHARSET_STRING);
		if (!data.isEmpty()) {
			Set<Entry<String, Object>> entries = data.entrySet();
			List<NameValuePair> parameters = new ArrayList<NameValuePair>(entries.size());
			for (Entry<String, ?> entry : entries) {
				if (entry.getValue() != null) {
					parameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
				}
			}
			HttpEntity entryEntity = new UrlEncodedFormEntity(parameters, DEFAULT_CHARSET);
			request.setEntity(entryEntity);
		}
		HttpResponse response;
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
		response = httpclient.execute(request);
		RequestResult requestResult = buildRequestResult(response);
		return requestResult;
	}
}
