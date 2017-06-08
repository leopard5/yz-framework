package com.yz.framework.bizlogging.consumer.model;

import java.io.Serializable;

public class ApiInvokeLogPageQuery extends PageQuery implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8825085391062136056L;

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	private String api;

}
