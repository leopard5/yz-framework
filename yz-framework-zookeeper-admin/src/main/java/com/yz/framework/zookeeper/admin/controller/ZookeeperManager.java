package com.yz.framework.zookeeper.admin.controller;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import com.yz.framework.logging.Logger;
import com.yz.framework.zookeeper.TreeNode;
import com.yz.framework.zookeeper.ZK;

public class ZookeeperManager implements DisposableBean, ApplicationListener<ApplicationEvent> {

	private final static Logger LOGGER = Logger.getLogger(ZookeeperManager.class);
	private int baseSleepTimeMs;
	private int sessionTimeOutMs;
	private String hosts;

	public int getBaseSleepTimeMs() {
		return baseSleepTimeMs;
	}

	public void setBaseSleepTimeMs(int baseSleepTimeMs) {
		this.baseSleepTimeMs = baseSleepTimeMs;
	}

	private int maxRetries;

	public int getSessionTimeOutMs() {
		return sessionTimeOutMs;
	}

	public void setSessionTimeOutMs(int sessionTimeOutMs) {
		this.sessionTimeOutMs = sessionTimeOutMs;
	}

	public String getHosts() {
		return hosts;
	}

	public void setHosts(String hosts) {
		this.hosts = hosts;
	}

	public int getMaxRetries() {
		return maxRetries;
	}

	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	private ZK client;

	public void start() {
		synchronized (this) {
			if (client != null && client.getState().isAlive()) {
				return;
			}
			try {
				client = new ZK(hosts, this.sessionTimeOutMs);
			} catch (IOException e) {
				LOGGER.error("start", e);
			}
		}

	}

	public String getNodeData(String path) {
		return getNodeData(path, new Stat());
	}

	public String getNodeData(String path, Stat stat) {
		try {
			byte[] data = client.getData(path, false, stat);
			if (stat != null && data != null) {
				return new String(data);
			}
			return null;
		} catch (Exception e) {
			LOGGER.error("getNodeData", e);
			return null;
		}
	}

	public List<ACL> getACL(String path) {
		try {
			Stat stat = new Stat();
			List<ACL> acls = client.getACL(path, stat);
			return acls;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String delete(String fullPath) {
		try {
			client.deleteChildrenIfExists(fullPath, -1);
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public String setNodeData(String fullPath, String data) {
		try {
			Stat stat = client.exists(fullPath, false);
			if (stat != null) {
				client.setData(fullPath, data.getBytes(), -1);
			} else {
				return "节点不存在";
			}
			return "";
		} catch (Exception e) {
			start();
			e.printStackTrace();
			return e.getMessage();
		}
	}

	public TreeNode addNode(String parentFullPath, String path) {
		try {
			String fullPath = client.getFullPath(parentFullPath, path);
			client.createParentNodeIfExists(fullPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			return client.getNode(fullPath);
		} catch (Exception e) {
			LOGGER.error("addNode", "创建Zookeeper节点失败", e);
			return null;
		}
	}

	public TreeNode getTree(String rootPath) {
		try {
			return client.getTree(rootPath);
		} catch (Exception e) {
			LOGGER.error("getTree", "获取zookeeper失败", e);
			start();
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof ContextRefreshedEvent) {
			this.start();
		}
	}

	@Override
	public void destroy() throws Exception {
		if (this.client != null) {
			try {
				this.client.close();
				this.client = null;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
