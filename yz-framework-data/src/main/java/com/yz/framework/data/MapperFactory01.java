package com.yz.framework.data;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class MapperFactory01 {

	private SqlSessionFactory masterSqlSessionFactory;
	private SqlSessionFactory slaveSqlSessionFactory;
	
	public SqlSessionFactory getMasterSqlSessionFactory() {
		return masterSqlSessionFactory;
	}

	public void setMasterSqlSessionFactory(SqlSessionFactory masterSqlSessionFactory) {
		this.masterSqlSessionFactory = masterSqlSessionFactory;
	}

	public SqlSessionFactory getSlaveSqlSessionFactory() {
		return slaveSqlSessionFactory;
	}

	public void setSlaveSqlSessionFactory(SqlSessionFactory slaveSqlSessionFactory) {
		this.slaveSqlSessionFactory = slaveSqlSessionFactory;
	}


	public <T> T getMapperForMaster(Class<T> type) {
		return masterSqlSessionFactory.openSession().getMapper(type);
	}
	public <T> T getMapper(Class<T> type) {
		return masterSqlSessionFactory.openSession().getMapper(type);
	}
	
	public <T> T getMapperForSlave(Class<T> type) {
		return slaveSqlSessionFactory.openSession().getMapper(type);
	}

	@Resource
	private PlatformTransactionManager tranactionManager;
	
	private final static ThreadLocal<TransactionStatus> transactionstatusLocal = new ThreadLocal<TransactionStatus>();
	private final static DefaultTransactionDefinition def = new DefaultTransactionDefinition();
	static {
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	}

	public TransactionStatus beginTransaction() {
		return beginTransaction(def);
	}

	/**
	 * @param definition
	 * @return
	 */
	public TransactionStatus beginTransaction(DefaultTransactionDefinition definition) {
		TransactionStatus transactionStatus = transactionstatusLocal.get();
		if (transactionStatus == null) {
			transactionStatus = tranactionManager.getTransaction(definition);
			transactionstatusLocal.set(transactionStatus);
		}
		return transactionStatus;
	}

	public void commitTransaction() {
		TransactionStatus transactionStatus = transactionstatusLocal.get();
		if (transactionStatus != null) {
			if (!transactionStatus.isCompleted()) {
				tranactionManager.commit(transactionStatus);
			}
			transactionstatusLocal.remove();
		}
	}
	
	public void commitTransaction(TransactionStatus transactionStatus) {
		if (transactionStatus != null) {
			if (!transactionStatus.isCompleted()) {
				tranactionManager.commit(transactionStatus);
			}
			transactionstatusLocal.remove();
		}
	}
	

	public void rollbackTransaction() {
		TransactionStatus transactionStatus = transactionstatusLocal.get();
		if (transactionStatus != null) {
			if (!transactionStatus.isCompleted()) {
				tranactionManager.rollback(transactionStatus);
			}
			transactionstatusLocal.remove();
		}
	}

	
}
