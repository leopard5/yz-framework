package com.yz.framework.logging;

public enum LogType {
	/**
	 * @Fields GENERAL : 正常日志
	 */
	GENERAL((byte) 1),
	/**
	 * @Fields BIZ_DATA : 业务日志
	 */
	BIZ_DATA((byte) 2),

	/**
	 * @Fields IN : 入口日志
	 */
	IN((byte) 3),
	/**
	 * @Fields OUT : 出口日志
	 */
	OUT((byte) 4),
	/**
	 * @Fields API_REQUEST : API调用请求日志
	 */
	API_REQUEST((byte) 5),
	/**
	 * @Fields API_RETURN : API调用请求返回结果日志
	 */
	API_RETURN((byte) 6);

	private byte value;

	private LogType(byte val) {
		this.setValue(val);
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
}
