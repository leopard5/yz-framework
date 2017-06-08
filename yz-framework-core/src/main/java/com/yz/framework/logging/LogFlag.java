package com.yz.framework.logging;

public enum LogFlag {
	NORMAL(1), // 正常日志
	ERROR(2), // 错误日志
	DATA(3); // 存储日志（只做存储/查询，不做显示，不进入统计）3
	private int value;

	private LogFlag(int val) {
		this.setValue(val);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
