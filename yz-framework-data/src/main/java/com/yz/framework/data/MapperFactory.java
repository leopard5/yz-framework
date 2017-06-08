package com.yz.framework.data;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

import com.yz.framework.logging.Logger;

public class MapperFactory implements InitializingBean, ApplicationContextAware {
	private final static Logger logger = Logger.getLogger(MapperFactory.class);

	private ApplicationContext masterApplicationContext;
	private ApplicationContext slaveApplicationContext;
	private PlatformTransactionManager tranactionManager;
	private String masterConfigLoation;
	private String slaveConfigLoation;
	private SqlSessionFactory masterSqlSessionFactory;

	private static MapperFactory instance;

	public MapperFactory() {
	}

	public MapperFactory(String masterConfigLoation, String slaveConfigLoation) {
		instance = this;
		this.setMasterConfigLoation(masterConfigLoation);
		this.setSlaveConfigLoation(slaveConfigLoation);
		startSubApplicationContext();
	}

	public String getMasterConfigLoation() {
		return masterConfigLoation;
	}

	public void setMasterConfigLoation(String masterConfigLoation) {
		this.masterConfigLoation = masterConfigLoation;
	}

	public String getSlaveConfigLoation() {
		return slaveConfigLoation;
	}

	public ResultMap getResultMap(String resultId) {
		return masterSqlSessionFactory.getConfiguration().getResultMap(resultId);
	}

	public String getReturnColumns(String sqlId, String returnProperties) {
		if (StringUtils.isEmpty(returnProperties)) {
			return null;
		}
		ResultMap resultMap = masterSqlSessionFactory.getConfiguration().getMappedStatement(sqlId).getResultMaps().get(0);
		StringBuilder sBuilder = new StringBuilder();
		String[] properties = returnProperties.split("\\,");
		for (String property : properties) {
			for (ResultMapping mapping : resultMap.getResultMappings()) {
				if (mapping.getProperty().equalsIgnoreCase(property)) {
					sBuilder.append(mapping.getColumn() + ",");
					break;
				}
			}
		}
		if (sBuilder.length() > 0) {
			return sBuilder.substring(0, (sBuilder.length() - 1));
		}
		return null;
	}

	public void setSlaveConfigLoation(String slaveConfigLoation) {
		this.slaveConfigLoation = slaveConfigLoation;
	}

	public <T> T getMapperForMaster(Class<T> type) {
		// return masterBeanFactory.getBean(type);
		T t = masterApplicationContext.getBean(type);
		if (t == null) {
			logger.error("no bean found for type " + type.getName());
		}
		return t;
	}

	public <T> T getMapper(Class<T> type) {
		return getMapperForMaster(type);
	}

	public <T> T getMapperForSlave(Class<T> type) {
		T t = slaveApplicationContext.getBean(type);
		if (t == null) {
			logger.error("no bean found for type " + type.getName());
		}
		return t;
	}

	private final static ThreadLocal<CounterTransactionStatus> transactionstatusLocal = new ThreadLocal<CounterTransactionStatus>();

	private final static DefaultTransactionDefinition def = new DefaultTransactionDefinition();
	static {
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		// def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
	}

	public TransactionStatus beginTransaction() {
		return beginTransaction(def);
	}

	/**
	 * @param definition
	 * @return
	 */
	public TransactionStatus beginTransaction(DefaultTransactionDefinition definition) {
		CounterTransactionStatus counterTransactionStatus = transactionstatusLocal.get();
		if (counterTransactionStatus == null) {
			counterTransactionStatus = new CounterTransactionStatus(tranactionManager.getTransaction(definition));
			transactionstatusLocal.set(counterTransactionStatus);
		}
		counterTransactionStatus.inreaseCounter();
		return counterTransactionStatus.getTransactionStatus();
	}

	public void commitTransaction() {
		CounterTransactionStatus counterTransactionStatus = transactionstatusLocal.get();
		if (counterTransactionStatus == null) {
			return;
		}
		if (counterTransactionStatus.getCounter() < 2) {
			try {
				TransactionStatus transactionStatus = counterTransactionStatus.getTransactionStatus();
				if (!transactionStatus.isCompleted()) {
					tranactionManager.commit(transactionStatus);
				}
			} finally {
				transactionstatusLocal.remove();
			}
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
		try {
			CounterTransactionStatus counterTransactionStatus = transactionstatusLocal.get();
			if (counterTransactionStatus == null) {
				return;
			}
			counterTransactionStatus.decreaseCounter();
			if (counterTransactionStatus.getCounter() == 0) {
				doRollback(counterTransactionStatus);
			}

		} catch (Exception e) {
			logger.error("rollbackTransaction", "回滚事务失败", e);
		}
	}

	private void doRollback(CounterTransactionStatus counterTransactionStatus) {
		try {
			TransactionStatus transactionStatus = counterTransactionStatus.getTransactionStatus();
			if (transactionStatus != null && !transactionStatus.isCompleted()) {
				tranactionManager.rollback(transactionStatus);
			}
		} finally {
			transactionstatusLocal.remove();
		}
	}

	private ApplicationContext buildApplicationContext(String configLocation) {
		logger.info("buildApplicationContext", configLocation);
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(
				new String[] { configLocation },
				parentApplicationContext);
		return appContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (masterApplicationContext == null) {
			startSubApplicationContext();
		}
	}

	private void startSubApplicationContext() {
		if (StringUtils.hasLength(masterConfigLoation)) {
			logger.info("startSubApplicationContext", "初始化Master数据容器开始");
			masterApplicationContext = buildApplicationContext(masterConfigLoation);
			setMasterSqlSessionFactory(masterApplicationContext.getBean(SqlSessionFactory.class));
			logger.info("startSubApplicationContext", "初始化Master数据容器完成");
			tranactionManager = masterApplicationContext.getBean(PlatformTransactionManager.class);
		}
		if (StringUtils.hasLength(slaveConfigLoation)) {
			logger.info("startSubApplicationContext", "初始化Slave数据容器开始");
			slaveApplicationContext = buildApplicationContext(slaveConfigLoation);
			logger.info("startSubApplicationContext", "初始化Slave数据容器完成");
		}
	}

	private ApplicationContext parentApplicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.parentApplicationContext = applicationContext;
	}

	public boolean realRollbackTransaction() {
		CounterTransactionStatus counterTransactionStatus = transactionstatusLocal.get();
		if (counterTransactionStatus != null) {
			doRollback(counterTransactionStatus);
			return true;
		}
		return false;
	}

	/**
	 * 回滚未提交的事务
	 * 
	 * @throws
	 * @author yazhong.qi
	 * @since JDK 1.7
	 * @date 2016年2月3日 下午1:40:47
	 */
	public static boolean checkAndRollbackTransaction() {
		return instance.realRollbackTransaction();
	}

	public SqlSessionFactory getMasterSqlSessionFactory() {
		return masterSqlSessionFactory;
	}

	public void setMasterSqlSessionFactory(SqlSessionFactory masterSqlSessionFactory) {
		this.masterSqlSessionFactory = masterSqlSessionFactory;
	}
}
