package com.yz.framework.config;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.yz.framework.logging.Logger;

public class ConfigConnectionStateWatcher implements Watcher {
	private final static Logger logger = Logger.getLogger(ConfigConnectionStateWatcher.class);
	private PropertyRegistry propertyRegistry;

	// 0:未连接，1：连接，2：连接后断开，3：session过期，4：其他状态
	private final static AtomicInteger zkConnectionState = new AtomicInteger(0);

	public ConfigConnectionStateWatcher(PropertyRegistry propertyRegistry) {
		this.propertyRegistry = propertyRegistry;
	}

	@Override
	public void process(WatchedEvent event) {

		if (event.getState() == KeeperState.Disconnected) {

			if (zkConnectionState.intValue() == 1) {
				// 从连接变成断开；
				logger.error("process", "zookeeper处于断开状态");
				zkConnectionState.compareAndSet(1, 2);
			}
			else {
				logger.info("process", "zookeeper处于断开状态");
			}
		} else if (event.getState() == KeeperState.Expired) {
			if (zkConnectionState.intValue() == 1) {
				logger.error("process", "Zookeeper会话过期");
				zkConnectionState.compareAndSet(1, 3);
			}
			else {
				logger.info("process", "Zookeeper会话过期");
			}
			this.propertyRegistry.startLoadPropertiesFromZK();
		} else if (event.getState() == KeeperState.SyncConnected) {
			if (zkConnectionState.intValue() != 0 && zkConnectionState.intValue() != 1) {
				logger.error("process", "zookeeper已经连接上");
			} else {
				logger.info("process", "zookeeper已经连接上");
			}
			zkConnectionState.set(1);
		}
		else {
			if (zkConnectionState.intValue() == 1) {
				logger.error("process", "zookeeper处于状态： " + event.getState());
				zkConnectionState.compareAndSet(1, 4);
			}
			else {
				logger.warn("process", "zookeeper处于状态： " + event.getState());
			}

		}
	}
}
