package com.yz.framework.config;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import com.yz.framework.logging.Logger;

public class ConfigNodeWatcher implements Watcher {
	private final static Logger LOGGER = Logger.getLogger(ConfigNodeWatcher.class);
	private PropertyRegistry propertyRegistry;

	public ConfigNodeWatcher(PropertyRegistry propertyRegistry) {
		this.propertyRegistry = propertyRegistry;
	}

	@Override
	public void process(WatchedEvent event) {
		EventType eventType = event.getType();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("receive the notify event,the event type is " + eventType);
		}
		if (eventType == EventType.None) {
			return;
		}
		String path = event.getPath();
		if (eventType == EventType.NodeDataChanged) {
			this.propertyRegistry.reloadPropertyForPath(path);
		} else if (eventType == EventType.NodeChildrenChanged) {
			this.propertyRegistry.reloadChildPropertiesForPath(path);
		} else if (eventType == EventType.NodeDeleted) {
			this.propertyRegistry.deletePropertiesForPath(path);
		}
	}
}
