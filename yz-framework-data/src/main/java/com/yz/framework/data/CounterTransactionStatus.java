/**
 * yz-framework-data 
 * CounterTransactionStatus.java 
 * com.yz.framework.data 
 * TODO  
 * @author yazhong.qi
 * @date   2015年12月7日 上午11:42:38 
 * @version   1.0
 */

package com.yz.framework.data;

import org.springframework.transaction.TransactionStatus;

/**
 * ClassName:CounterTransactionStatus <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月7日 上午11:42:38 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class CounterTransactionStatus {

	public CounterTransactionStatus(TransactionStatus transactionStatus) {
		setTransactionStatus(transactionStatus);
		this.counter = 0;

	}

	private TransactionStatus transactionStatus;
	private int counter;

	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public int getCounter() {
		return counter;
	}

	public void inreaseCounter() {
		counter = counter + 1;
	}

	public void decreaseCounter() {
		counter = counter - 1;
	}

}
