/**
 * yz-framework-search 
 * IndexService.java 
 * com.yz.framework.search 
 * TODO  
 * @author yazhong.qi
 * @date   2015年12月15日 下午3:38:05 
 * @version   1.0
 */

package com.yz.framework.search;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;

/**
 * ClassName:IndexService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月15日 下午3:38:05 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class IndexService {

	private String serviceUrl;

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	protected SolrClient getSolrClient() {
		return new HttpSolrClient(serviceUrl);
	}

	private static final Logger LOGGER = Logger.getLogger(IndexService.class);

	public void addIndex(List<Map<String, Object>> dataList) throws IOException, SolrServerException
	{
		SolrClient server = null;
		try {
			if (!dataList.isEmpty()) {
				Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
				for (Map<String, Object> map : dataList) {

					Set<Entry<String, Object>> entries = map.entrySet();
					SolrInputDocument doc = new SolrInputDocument();
					for (Entry<String, Object> entry : entries) {
						Object valueObject = entry.getValue();
						if (valueObject == null) {
							continue;
						}
						if (valueObject instanceof java.math.BigInteger) {
							doc.addField(entry.getKey(), ((BigInteger) valueObject).longValue());
						}
						else if (valueObject instanceof java.math.BigDecimal) {
							doc.addField(entry.getKey(), ((BigDecimal) valueObject).doubleValue());
						}
						else {
							doc.addField(entry.getKey(), valueObject);
						}
					}
					docs.add(doc);
				}
				server = getSolrClient();

				server.add(docs);
				server.commit();
			}
			LOGGER.info("生成索引" + dataList.size() + "条");

		} finally {
			if (server != null) {
				server.close();
			}
		}
	}

	public void deleteByQuery(String query) throws IOException, SolrServerException
	{
		SolrClient server = null;
		try {
			server = getSolrClient();
			UpdateResponse updateResponse = server.deleteByQuery(query);
			server.commit();

		} finally {
			if (server != null) {
				server.close();
			}
		}
	}

	public void updateIndex(List<Map<String, Object>> addIndexList, List<String> deleteIdList) throws IOException, SolrServerException
	{
		SolrClient server = null;
		try {
			if (!addIndexList.isEmpty() || !deleteIdList.isEmpty()) {
				Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
				for (Map<String, Object> map : addIndexList) {
					Set<Entry<String, Object>> entries = map.entrySet();
					SolrInputDocument doc = new SolrInputDocument();
					for (Entry<String, Object> entry : entries) {
						Object valueObject = entry.getValue();
						if (valueObject == null) {
							continue;
						}
						doc.addField(entry.getKey(), entry.getValue());
					}
					docs.add(doc);
				}
				server = getSolrClient();
				if (!docs.isEmpty()) {
					server.add(docs);
					LOGGER.info("更新/添加索引" + addIndexList.size() + "条");
				}
				if (!deleteIdList.isEmpty()) {
					server.deleteById(deleteIdList);
					LOGGER.info("删除索引" + deleteIdList.size() + "条");
				}
				server.commit();
			}

		} finally {
			if (server != null) {
				server.close();
			}
		}
	}

	public void deleteIndex(List<String> ids) throws IOException, SolrServerException
	{
		SolrClient server = null;
		try {
			if (!ids.isEmpty()) {
				server = getSolrClient();
				server.deleteById(ids);
				server.commit();
			}
			LOGGER.info("删除索引" + ids.size() + "条");

		} finally {
			if (server != null) {
				server.close();
			}
		}
	}
}
