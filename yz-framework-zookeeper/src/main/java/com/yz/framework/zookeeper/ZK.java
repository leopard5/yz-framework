package com.yz.framework.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

public class ZK extends ZooKeeper {

	public static final String ZOOKEEPER_PATH_DELIITER = "/";
	private int retryTimes = 10;
	private int baseSleepTimeMs = 200;

	public ZK(
			String connectString, int sessionTimeout) throws IOException {
		super(connectString, sessionTimeout, defaultIfNull(null));
	}

	public ZK(
			String connectString, int sessionTimeout, Watcher watcher) throws IOException {
		super(connectString, sessionTimeout, defaultIfNull(watcher));
	}

	private static Watcher defaultIfNull(Watcher watcher) {
		if (watcher == null) {
			watcher = new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					// do nothing
				}
			};
		}
		return watcher;
	}

	public ZK(
			String connectString, int sessionTimeout, Watcher watcher, int retryTimes, int baseSleepTimeMs)
			throws IOException {
		super(connectString, sessionTimeout, watcher);
		this.retryTimes = retryTimes;
		this.baseSleepTimeMs = baseSleepTimeMs;
	}

	/**
	 * 创建节点，如果父节点不存在，则创建父节点
	 * 
	 * @param path
	 * @param data
	 * @param acl
	 * @param createMode
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void createParentNodeIfExists(
			final String path,
			byte data[],
			List<ACL> acl,
			CreateMode createMode)
			throws KeeperException, InterruptedException {
		makeSureConnected();
		createRecursive(path, data, acl, createMode);
	}

	private void createRecursive(final String path, byte data[], List<ACL> acl, CreateMode createMode)
			throws KeeperException, InterruptedException {
		Stat stat = exists(path, false);
		if (stat != null) {
			return;
		}
		String parentPath = getParentPath(path);
		if (parentPath != ZK.ZOOKEEPER_PATH_DELIITER) {
			createRecursive(parentPath, null, acl, createMode);
		}
		this.create(path, data, acl, createMode);
	}

	private String getParentPath(String path) {
		int lastIndex = path.lastIndexOf(ZK.ZOOKEEPER_PATH_DELIITER);
		if (lastIndex > 0) {
			return path.substring(0, lastIndex);
		}
		return ZK.ZOOKEEPER_PATH_DELIITER;
	}

	/**
	 * 删除节点，如果存在子节点，连同子节点一起删除
	 * 
	 * @param path
	 * @param version
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void deleteChildrenIfExists(final String path, int version) throws KeeperException, InterruptedException {
		makeSureConnected();
		deleteRecursive(path, version);

	}

	private void deleteRecursive(final String path, int version) throws KeeperException, InterruptedException {
		Stat stat = this.exists(path, false);
		if (stat != null) {
			if (stat.getNumChildren() > 0) {
				List<String> children = this.getChildren(path, false);
				for (String childPath : children) {
					String fullPath = getFullPath(path, childPath);
					deleteRecursive(fullPath, version);
				}
			}
			this.delete(path, version);
		}
	}

	public TreeNode getTree(String rootPath) throws KeeperException, InterruptedException {
		makeSureConnected();
		if (this.isConnected()) {
			TreeNode node = new TreeNode();
			node.setText(rootPath);
			node.setFullPath(rootPath);
			loadChildren(node);
			return node;
		}
		return null;
	}

	private void loadChildren(TreeNode pathNode) throws KeeperException, InterruptedException {
		List<String> children = this.getChildren(pathNode.getFullPath(), false);
		pathNode.setChildren(new ArrayList<TreeNode>());
		if (children.size() == 0) {
			return;
		}
		pathNode.setChildren(new ArrayList<TreeNode>());
		for (String path : children) {
			TreeNode node = new TreeNode();
			String fullPath = getFullPath(pathNode.getFullPath(), path);
			node.setFullPath(fullPath);
			node.setText(path);
			Stat stat = new Stat();
			String data = getNodeData(fullPath, stat);
			node.setData(data);
			node.setId(stat.getCzxid());
			pathNode.getChildren().add(node);
			if (stat.getNumChildren() > 0) {
				loadChildren(node);
			}
		}

	}

	public String getNodeData(String path, Stat stat) throws KeeperException, InterruptedException {
		makeSureConnected();
		byte[] data = this.getData(path, false, stat);
		if (stat != null && data != null) {
			return new String(data);
		}
		return null;
	}

	public String getFullPath(String path, String childPath) {

		if (path == null || path.trim().length() == 0 || path.equalsIgnoreCase(ZK.ZOOKEEPER_PATH_DELIITER)) {
			return ZK.ZOOKEEPER_PATH_DELIITER + childPath;
		} else {
			return path + ZK.ZOOKEEPER_PATH_DELIITER + childPath;
		}
	}

	public TreeNode getNode(String fullPath) throws KeeperException, InterruptedException {
		makeSureConnected();
		Stat stat = exists(fullPath, false);
		if (stat != null) {
			TreeNode node = new TreeNode();
			node.setFullPath(fullPath);
			node.setText(getPathName(fullPath));
			node.setId(stat.getCzxid());
			byte[] data = this.getData(fullPath, false, stat);
			if (stat != null && data != null) {
				node.setData(new String(data));
			}
			return node;
		}
		return null;
	}

	private String getPathName(String path) {

		int lastIndex = path.lastIndexOf('/');
		return path.substring(lastIndex + 1);
	}

	public boolean isConnected() {
		States states = getState();
		return states != null && states.isConnected();
	}

	// 确保已经连接上zookeeper
	public void makeSureConnected() {
		States states = this.getState();
		if (states.isConnected()) {
			return;
		}
		if (!states.isAlive()) {
			synchronized (this) {
				if (!states.isAlive()) {
					this.cnxn.start();
				}
			}
		}
		int times = 0;
		while (!this.isConnected() && times < retryTimes) {
			try {
				Thread.sleep(baseSleepTimeMs);// 等待连接
				times++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean exists(String path) throws KeeperException, InterruptedException {
		this.makeSureConnected();
		Stat stat = this.exists(path, false);
		return stat != null;

	}

	public String createParentNodeIfExists(String path, byte[] data) throws KeeperException, InterruptedException {
		return create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

	}

	public void setData(String path, String data) throws KeeperException, InterruptedException {
		if (data == null) {
			setData(path, null, -1);
		} else {
			setData(path, data.getBytes(), -1);
		}
	}

	public void start() {
		makeSureConnected();
	}

	public String getStringData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
		byte[] data = getData(path, watcher, stat);
		if (data != null) {
			return new String(data);
		}
		return null;
	}
}
