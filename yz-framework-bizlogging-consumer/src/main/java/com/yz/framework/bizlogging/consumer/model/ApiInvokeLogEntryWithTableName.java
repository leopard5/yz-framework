package com.yz.framework.bizlogging.consumer.model;

import com.yz.framework.bizlogging.ApiInvokeLogEntry;

public class ApiInvokeLogEntryWithTableName extends ApiInvokeLogEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1777078369646406662L;

	private String tableName;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
