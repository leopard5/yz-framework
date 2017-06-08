package com.yz.framework.config;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import com.yz.framework.logging.Logger;

public class PropertiesPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements DisposableBean {

	private final static Logger LOGGER = Logger.getLogger(PropertiesPlaceholderConfigurer.class);

	/**
	 * 是否从zookeeper读取配置信息
	 */
	private boolean enableZookeeperConfig;

	public boolean isEnableZookeeperConfig() {
		return enableZookeeperConfig;
	}

	public void setEnableZookeeperConfig(boolean enableZookeeperConfig) {
		this.enableZookeeperConfig = enableZookeeperConfig;
	}

	@Override
	protected Properties mergeProperties() throws IOException {
		Properties localProperties = super.mergeProperties();
		PropertiesRegistry.SINGLETON.mergeProperties(localProperties);
		if (enableZookeeperConfig) {
			PropertiesRegistry.SINGLETON.loadPropertiesFromZookeeper();
		}
		return PropertiesRegistry.SINGLETON.getAllProperties();
	}

	@Override
	public void destroy() throws Exception {
		if (this.enableZookeeperConfig) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("stop zookeeper gracefully");
			}
			PropertiesRegistry.SINGLETON.stop();
		}
	}
}
