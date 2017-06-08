package com.yz.framework.data;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;

public class EncryptBasicDataSource extends BasicDataSource implements InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO 自动生成的方法存根
		//String userName = getUsername();
		//
	}
}
