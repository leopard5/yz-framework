package com.yz.framework.bizlogging.consumer.model;

public class PageQuery {

	private int page;
	private int rows;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getSkip() {
		return (page-1) * rows;
	}

	

}
