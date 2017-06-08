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

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

/**
 * TODO
 * 
 * @author yazhong.qi
 * @date 2016年1月14日 下午5:00:12
 * @version 1.0
 */
public class CloudIndexService extends IndexService {

	private String zkHosts;
	private Integer zkClientTimeout;
	public Integer getZkClientTimeout() {
		return zkClientTimeout;
	}

	public void setZkClientTimeout(Integer zkClientTimeout) {
		this.zkClientTimeout = zkClientTimeout;
	}

	private String collectionName;

	public String getZkHosts() {
		return zkHosts;
	}

	public void setZkHosts(String zkHosts) {
		this.zkHosts = zkHosts;
	}

	@Override
	protected SolrClient getSolrClient() {
		CloudSolrClient cloudSolrClient = new CloudSolrClient(zkHosts);
		cloudSolrClient.setDefaultCollection(collectionName);
		cloudSolrClient.setZkClientTimeout(zkClientTimeout);
		cloudSolrClient.connect();
		//cloudSolrClient.setParallelUpdates(true);
		return cloudSolrClient;
	}

	/**
	 * collectionName.
	 *
	 * @return the collectionName
	 */
	public String getCollectionName() {
		return collectionName;
	}

	/**
	 * collectionName.
	 *
	 * @param collectionName
	 *            the collectionName to set
	 */
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

}
