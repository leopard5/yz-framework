package com.yz.framework.config;

import com.yz.framework.logging.Logger;
import com.yz.framework.util.FileUtil;
import com.yz.framework.zookeeper.ZK;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class PropertiesRegistry {

    private final static Logger LOGGER = Logger.getLogger(Properties.class);

    private ZK zk;

    // private static final Properties allProperties = new Properties();

    private final static String CONFIG_ROOT = "/conf";

    private String hosts;
    private String configName;

    /**
     * @Fields SESSION_TIMEOUT_MS : Session过期时间
     */

    private final static int SESSION_TIMEOUT_MS = Integer.MAX_VALUE;
    /**
     * @Fields BASE_SLEEP_TIMEMS : 重连间隔时间
     */
    private final static int BASE_SLEEP_TIMEMS = 1000;
    /**
     * @Fields MAX_RETRIES : 重连次数
     */
    private final static int MAX_RETRIES = 10;
    private final static String ZOO_HOSTS_KEY = "zookeeper.config.hosts";
    private final static String CONFIG_NAME = "config.fileName";

    // private final static String ZOO_CONFIG = "zoo.properties";

    public static final PropertiesRegistry SINGLETON = new PropertiesRegistry();

    /**
     * 私有构造函数，保证单例
     */
    private PropertiesRegistry() {

    }

    private void connect() {
        if (zk != null && zk.getState().isAlive()) {
            return;
        } else {
            Watcher watcher = new ConnectionStateWatcher(this);
            try {
                zk = new ZK(hosts,
                        SESSION_TIMEOUT_MS,
                        watcher,
                        MAX_RETRIES,
                        BASE_SLEEP_TIMEMS);
            } catch (IOException e) {

                e.printStackTrace();

            }
        }
    }

    public synchronized void mergeProperties(Properties properties) {
        CollectionUtils.mergePropertiesIntoMap(properties, ConfigManager.ALL_PROPERTIES);

    }

    /**
     * 启动从zookeeper读取配置信息
     *
     * @throws IOException
     * @throws InterruptedException
     * @throws KeeperException
     */
    public void loadPropertiesFromZookeeper() {
        this.hosts = ConfigManager.ALL_PROPERTIES.get(ZOO_HOSTS_KEY);
        this.configName = ConfigManager.ALL_PROPERTIES.get(CONFIG_NAME);
        connect();
        try {
            zk.makeSureConnected();
            doLoadProperties();
        } catch (KeeperException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        // return new Properties(ConfigManager.ALL_PROPERTIES);
    }

    private void doLoadProperties() throws KeeperException, InterruptedException {
        Map<String, String> propertiesFromZookeeper = loadPropertiesForPath();
        ConfigManager.ALL_PROPERTIES.putAll(propertiesFromZookeeper);
        writeToLocalFile();
    }

    private Map<String, String> loadPropertiesForPath() throws KeeperException, InterruptedException {
        String fullPath = zk.getFullPath(CONFIG_ROOT, configName);
        Watcher dataChangeWatcher = new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                try {
                    doLoadProperties();
                } catch (KeeperException e) {
                    connect();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
        Stat stat = zk.exists(fullPath, dataChangeWatcher);
        Map<String, String> properties = null;
        if (stat != null) {
            String propertieString = zk.getStringData(fullPath, dataChangeWatcher, stat);
            properties = loadPropertiesFromString(propertieString);
        } else {
            properties = new HashMap<String, String>();
        }
        return properties;
    }

    private Map<String, String> loadPropertiesFromString(String propertieString) {
        StringReader sReader = new StringReader(propertieString);
        Map<String, String> map = new HashMap<String, String>();
        try {
            Properties properties2 = new Properties();
            properties2.load(sReader);
            Set<Entry<Object, Object>> entries = properties2.entrySet();
            for (Entry<Object, Object> entry : entries) {
                String value = entry.getValue() == null ? "" : entry.getValue().toString();
                map.put(entry.getKey().toString(), value);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        } finally {
            sReader.close();
        }
        return map;
    }

    /**
     * 持久化配置到本地配置文件
     */
    private synchronized void writeToLocalFile() {
        try {
            Set<String> keysObjects = ConfigManager.ALL_PROPERTIES.keySet();
            String[] keys = new String[keysObjects.size()];
            int i = 0;
            for (String key : keysObjects) {
                keys[i] = key;
                i++;
            }
            Arrays.sort(keys);
            ClassPathResource classPathResource = new ClassPathResource("local.properties");
            StringBuilder sb = new StringBuilder();
            for (Object key : keys) {
                sb.append(key + "=" + ConfigManager.ALL_PROPERTIES.get(key) + "\n");
            }
            File file = classPathResource.getFile();
            FileUtil.writeUseBio(file.getPath(), sb.toString());
        } catch (Exception e) {
            LOGGER.error("writeToLocaFile", "持久化配置信息到本地失败", e);
        }
    }

    /**
     * 关闭zookeeper连接
     */
    public synchronized void stop() {
        try {
            if (this.zk != null) {
                this.zk.close();
                this.zk = null;
            }
        } catch (InterruptedException e) {
            LOGGER.error("stop", "关闭zookeeper连接失败", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 根据属性名称获取属性值
     *
     * @param propertyName
     * @return
     */
    public static String getProperty(String propertyName) {
        Object valueObject = ConfigManager.ALL_PROPERTIES.get(propertyName);
        return valueObject == null ? null : valueObject.toString();
    }

    /**
     * 返回所有属性
     *
     * @return
     */
    public Properties getAllProperties() {
        Properties properties = new Properties();
        properties.putAll(ConfigManager.ALL_PROPERTIES);
        return properties;
    }

    public static class ConnectionStateWatcher implements Watcher {
        private PropertiesRegistry propertyRegistry;

        // 0:未连接，1：连接，2：连接后断开，3：session过期，4：其他状态
        private final static AtomicInteger zkConnectionState = new AtomicInteger(0);

        public ConnectionStateWatcher(PropertiesRegistry propertyRegistry) {
            this.propertyRegistry = propertyRegistry;
        }

        @Override
        public void process(WatchedEvent event) {

            if (event.getState() == KeeperState.Disconnected) {

                if (zkConnectionState.intValue() == 1) {
                    // 从连接变成断开；
                    LOGGER.error("process", "zookeeper处于断开状态");
                    zkConnectionState.compareAndSet(1, 2);
                } else {
                    LOGGER.info("process", "zookeeper处于断开状态");
                }
            } else if (event.getState() == KeeperState.Expired) {
                if (zkConnectionState.intValue() == 1) {
                    LOGGER.error("process", "Zookeeper会话过期");
                    zkConnectionState.compareAndSet(1, 3);
                } else {
                    LOGGER.info("process", "Zookeeper会话过期");
                }
                this.propertyRegistry.loadPropertiesFromZookeeper();
            } else if (event.getState() == KeeperState.SyncConnected) {
                if (zkConnectionState.intValue() != 0 && zkConnectionState.intValue() != 1) {
                    LOGGER.error("process", "zookeeper已经连接上");
                } else {
                    LOGGER.info("process", "zookeeper已经连接上");
                }
                zkConnectionState.set(1);
            } else {
                if (zkConnectionState.intValue() == 1) {
                    LOGGER.error("process", "zookeeper处于状态： " + event.getState());
                    zkConnectionState.compareAndSet(1, 4);
                } else {
                    LOGGER.warn("process", "zookeeper处于状态： " + event.getState());
                }

            }
        }
    }

}
