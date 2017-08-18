/**
 * yz-framework-zookeeper
 * LocalCacheFactory.java
 * com.yz.framework.zookeeper
 * TODO
 *
 * @author yazhong.qi
 * @date 2016年3月1日 上午11:55:38
 * @version 1.0
 */

package com.yz.framework.cache;

import com.yz.framework.logging.Logger;
import com.yz.framework.zookeeper.ZK;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ZookeeperLocalCacheFactory extends AbstractCacheFactory {

    private static final Map<String, Object> cachedObjectsMap = new ConcurrentHashMap<String, Object>();
    private static final Map<Object, Set<String>> dependecyMap = new ConcurrentHashMap<Object, Set<String>>();
    private static final Logger LOGGER = Logger.getLogger(ZookeeperLocalCacheFactory.class);
    private int baseSleepTimeMs = 1000;

    private String cacheBasePath;
    private String connectString;
    private int retryTimes = 3;
    private int sessionTimeout = Integer.MAX_VALUE;

    public void setCacheBasePath(String cacheBasePath) {
        this.cacheBasePath = cacheBasePath;
    }

    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    // 当通知清除缓存的时候，zookeeper不可用，把任务放入队列，等到zookeeper恢复后执行
    private final ArrayBlockingQueue<Object> updateDependencyQueue = new ArrayBlockingQueue<Object>(1000);
    private Thread updateDependencyWorker;
    private ZK zk;

    @Override
    public boolean add(String cacheObjectKey, Object cacheData, Date expiredAt, Long relativeExpiredMs, Object... dependencies) {

        // 添加监控成功后，才放入缓存
        if (addDependencyNodeChangeWatcher(dependencies)) {
            cachedObjectsMap.put(cacheObjectKey, cacheData);
            addDependencyMapping(cacheObjectKey, dependencies);
            return true;
        }
        return false;
    }

    private void addDependencyMapping(String cacheObjectKey, Object... dependencies) {
        for (Object dependencyKey : dependencies) {
            if (dependecyMap.containsKey(dependencyKey)) {
                dependecyMap.get(dependencyKey).add(cacheObjectKey);
            } else {
                Set<String> keySet = new HashSet<String>();
                keySet.add(cacheObjectKey);
                dependecyMap.put(dependencyKey, keySet);
            }
        }

    }

    /**
     * 为该依赖节点添加一个监听器，如果该节点不存在，则添加该节点
     *
     * @param dependecyKey
     *            void
     * @throws InterruptedException
     * @throws KeeperException
     * @throws
     * @author yazhong.qi
     * @since JDK 1.7
     * @date 2016年3月1日 下午6:00:26
     */
    private boolean addDependencyNodeChangeWatcher(Object... dependencies) {
        try {
            for (Object dependency : dependencies) {
                Watcher watcher = getNodeChangeWatcher(dependency);
                String path = zk.getFullPath(cacheBasePath, dependency.toString());
                Stat stat = new Stat();

                if (!zk.exists(path)) {
                    zk.create(path, String.valueOf(System.currentTimeMillis()).getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                zk.getData(path, watcher, stat);
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("addDependencyNodeChangeWatcher", "添加依赖到zookeeper失败", e);
            connect();
            return false;
        }
    }

    private void addToUpdateDependencyQueue(Object dependecyKey) {
        try {
            if (!updateDependencyQueue.contains(dependecyKey)) {
                updateDependencyQueue.put(dependecyKey);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public boolean clear() {

        // TODO Auto-generated method stub
        return false;
    }

    private void clearCacheObjectByDependency(Object dependency) {
        Set<String> keySet = dependecyMap.get(dependency);
        for (String key : keySet) {
            cachedObjectsMap.remove(key);
        }
    }

    private synchronized void connect() {
        if (zk != null && zk.getState().isAlive()) {
            // 如果Zookeeper没有死掉，则无需重连
            return;
        }
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                try {
                    handleEvent(event);
                } catch (Exception e) {
                }
            }
        };
        try {
            zk = new ZK(connectString, sessionTimeout, watcher, retryTimes, baseSleepTimeMs);
            zk.start();
        } catch (IOException e) {
            LOGGER.error("connect", "连接zookeeper失败", e);

        }
    }

    public synchronized void destroy() {
        try {
            cachedObjectsMap.clear();
            dependecyMap.clear();
            stopWorker();
            if (zk != null) {
                zk.close();
                zk = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
     * (非Javadoc) <p>Title: notifyCacheRefresh</p> <p>Description: </p>
     *
     * @param dependecyKey
     *
     * @see
     * com.yz.framework.zookeeper.CacheFactory#notifyCacheRefresh(java.lang
     * .String)
     */
    public void expire(Object dependency) {
        addToUpdateDependencyQueue(dependency);
    }

    public String getCacheBasePath() {
        return cacheBasePath;
    }

    public String getConnectString() {
        return connectString;
    }

    private Watcher getNodeChangeWatcher(final Object dependency) {
        Watcher watcher = new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println(event.toString());
                if (event.getType() == EventType.NodeDataChanged) {
                    clearCacheObjectByDependency(dependency);
                }
            }
        };
        return watcher;
    }

    /*
     * (非Javadoc) <p>Title: getObject</p> <p>Description: </p>
     *
     * @param cacheObjectkey
     *
     * @return
     *
     * @see com.yz.framework.zookeeper.CacheFactory#getObject(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(String cacheObjectkey) {
        // 只有当处于连接状态，方可启动缓存
        if (zk.getState().isConnected()) {
            Object obj = cachedObjectsMap.get(cacheObjectkey);
            return obj == null ? null : (T) cachedObjectsMap.get(cacheObjectkey);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getObject(String cacheObjectkey, Class<T> clazz) {
        return getObject(cacheObjectkey);
    }

    private Thread getRefreshDependecyWorker() {
        return new Thread(new Runnable() {
            public void run() {
                try {
                    // 当线程收到中断通知，自动结束
                    while (!Thread.interrupted()) {
                        final Object dependecyKey = updateDependencyQueue.take();
                        if (!updateDependecyNode(dependecyKey)) {
                            // 通知失败，重新放入队列，等待3s在执行
                            updateDependencyQueue.put(dependecyKey);
                            Thread.sleep(3000);
                        }
                    }
                } catch (InterruptedException e) {
                    // 该线程被终止
                    // do nothing
                }
            }
        });
    }

    protected void handleEvent(WatchedEvent event) {
        System.out.println(event.toString());
        KeeperState state = event.getState();
        // session过期之后自动重连
        if (state == KeeperState.Expired) {
            System.out.println("reconnect to zookeeper");
            connect();
        }
    }

    public synchronized void init() throws IOException, KeeperException, InterruptedException {
        updateDependencyWorker = getRefreshDependecyWorker();
        updateDependencyWorker.start();
        connect();
        zk.createParentNodeIfExists(cacheBasePath, "".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    private void stopWorker() {
        try {
            if (updateDependencyWorker != null) {
                updateDependencyWorker.interrupt();
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private boolean updateDependecyNode(Object dependency) {
        try {
            String path = zk.getFullPath(cacheBasePath, dependency.toString());
            zk.setData(path, String.valueOf(System.currentTimeMillis()));
            System.out.println("update cache dependecy " + dependency);
            return true;
        } catch (NoNodeException ex) {
            // do nothing
            return true;
        } catch (KeeperException e) {
            connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void remove(String cacheObjectKey) {
        cachedObjectsMap.remove(cacheObjectKey);
    }

}
