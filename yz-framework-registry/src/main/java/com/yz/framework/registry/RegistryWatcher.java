package com.yz.framework.registry;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;

import com.yz.framework.zookeeper.ZK;

public class RegistryWatcher implements Watcher {
	private ZK zk;

	public RegistryWatcher(ZK zk) {
		this.zk = zk;
	}

	@Override
	public void process(WatchedEvent event) {
		
	}

}
