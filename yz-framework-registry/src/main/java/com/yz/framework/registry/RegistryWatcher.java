package com.yz.framework.registry;

import com.yz.framework.zookeeper.ZK;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

public class RegistryWatcher implements Watcher {
	private ZK zk;

	public RegistryWatcher(ZK zk) {
		this.zk = zk;
	}

	@Override
	public void process(WatchedEvent event) {
		
	}

}
