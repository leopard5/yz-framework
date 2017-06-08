package com.yz.framework.bizlogging.consumer.model;

import java.io.Serializable;


public class BizLogPageQuery extends PageQuery implements Serializable {

	private static final long serialVersionUID = -5037400931453275936L;

	

	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	private String api;
	private String operator;
}
