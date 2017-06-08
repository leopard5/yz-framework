package com.yz.framework.data;

import java.util.Stack;

import org.springframework.transaction.TransactionStatus;

public class TransactionHolder extends ThreadLocal<Stack<TransactionStatus>> {

	@Override
	protected Stack<TransactionStatus> initialValue() {
		return new Stack<TransactionStatus>();
	}

}
