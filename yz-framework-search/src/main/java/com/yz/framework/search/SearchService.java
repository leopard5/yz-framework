package com.yz.framework.search;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.yz.framework.http.HttpRequestUtil;
import com.yz.framework.http.RequestResult;
import com.yz.framework.logging.Logger;
import com.yz.framework.util.StringUtil;

/**
 * TODO
 * 
 * @author yazhong.qi
 * @date 2015年12月13日 下午3:52:03
 * @version 1.0
 */
public class SearchService implements InitializingBean {

	private static final Logger LOGGER = Logger.getLogger(SearchService.class);
	private String serviceUrl;

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public RequestResult query(
			String keywords,
			Map<String, Object> filters,
			String returnFeilds,
			Integer pageIndex,
			Integer pageSize,
			String sortField,
			String sortDirection) throws Exception {

		StringBuilder sb = new StringBuilder();
		if (StringUtil.isNotBlank(keywords)) {
			sb.append("q=" + keywords);
		}
		else {
			sb.append("q=*:*");
		}
		if (filters != null && !filters.isEmpty()) {
			Set<Entry<String, Object>> entries = filters.entrySet();
			for (Entry<String, Object> entry : entries) {
				sb.append("&" + entry.getKey() + ":" + entry.getValue());
			}
		}
		if (StringUtil.isNotBlank(returnFeilds)) {
			sb.append("&fl=" + returnFeilds);
		}
		sb.append("&rows=" + pageSize + "&start=" + pageIndex);
		if (StringUtil.isNotBlank(sortField)) {
			sb.append("&sort=" + sortField + "+" + (StringUtil.isBlank(sortDirection) ? "desc" : sortDirection));
		}
		String url = serviceUrl + sb.toString();
		RequestResult requestResult = HttpRequestUtil.get(url, null);
		return requestResult;
	}

	public SearchResult search(
			String keywords,
			Map<String, Object> filters,
			String returnFeilds,
			Integer pageIndex,
			Integer pageSize,
			String sortField,
			String sortDirection) {
		SearchResult searchResult = null;
		try {
			RequestResult requestResult = query(keywords, filters, returnFeilds, pageIndex, pageSize, sortField, sortDirection);
			if (requestResult.success()) {
				searchResult = JSON.parseObject(requestResult.getBody(), SearchResult.class);
			}
			else {
				searchResult = new SearchResult();
				SearchError error = new SearchError();
				error.setCode(-1);
				error.setMsg(requestResult.getReasonPhrase());
				searchResult.setError(error);
			}
		} catch (Exception e) {
			searchResult = new SearchResult();
			SearchError error = new SearchError();
			error.setCode(-1);
			error.setMsg(e.getMessage());
			searchResult.setError(error);
		}
		return searchResult;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		if (StringUtil.isNotBlank(serviceUrl)) {
			if (!serviceUrl.endsWith("?")) {
				serviceUrl = serviceUrl + "?";
			}
		}

	}

}
