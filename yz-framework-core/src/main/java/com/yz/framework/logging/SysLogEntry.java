package com.yz.framework.logging;

import com.alibaba.fastjson.annotation.JSONField;

public class SysLogEntry {

	@JSONField(ordinal = 1)
	private String rid;

	@JSONField(ordinal = 2)
	private int level;

	@JSONField(ordinal = 3)
	private String type;

	@JSONField(ordinal = 4)
	private int flag;

	@JSONField(name = "extends", ordinal = 5)
	private Object[] extentions;

	public SysLogEntry(
			int level, String type, int flag, Object... extentions) {

		this.level = level;
		this.type = type;
		this.flag = flag;
		this.extentions = extentions;

	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public Object[] getExtentions() {
		return extentions;
	}

	public void setExtentions(Object[] extentions) {
		this.extentions = extentions;
	}

	/**
	 * rid.
	 *
	 * @return the rid
	 */
	public String getRid() {
		return rid;
	}

	/**
	 * rid.
	 *
	 * @param rid
	 *            the rid to set
	 */
	public void setRid(String rid) {
		this.rid = rid;
	}

}
