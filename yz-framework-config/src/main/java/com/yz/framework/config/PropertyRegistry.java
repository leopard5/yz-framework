package com.yz.framework.config;

import com.yz.framework.logging.Logger;
import com.yz.framework.util.FileUtil;
import com.yz.framework.util.ListUtil;
import com.yz.framework.zookeeper.ZK;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;

public class PropertyRegistry {

    private final static Logger LOGGER = Logger.getLogger(Properties.class);

    private ZK zk;

    public final static String PROPERTY_DELIMITER = ".";
    private final static String CONFIG_ROOT = "config";

    /**
     * @Fields SESSION_TIMEOUT_MS : Session过期时间
     */
    private final static int SESSION_TIMEOUT_MS = 20 * 60 * 1000;
    /**
     * @Fields BASE_SLEEP_TIMEMS : 重连间隔时间
     */
    private final static int BASE_SLEEP_TIMEMS = 1000;
    /**
     * @Fields MAX_RETRIES : 重连次数
     */
    private final static int MAX_RETRIES = 10;
    private final static String ZOO_HOSTS_KEY = "zookeeper.config.hosts";
    // private final static String ZOO_CONFIG = "zoo.properties";

    private String hosts;

    public static final PropertyRegistry INSTANCE = new PropertyRegistry();

    /**
     * 私有构造函数，保证单例
     */
    private PropertyRegistry() {
    }

    public synchronized void addProperties(Properties properties) {
        CollectionUtils.mergePropertiesIntoMap(properties, ConfigManager.ALL_PROPERTIES);
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
            for (String key : keys) {
                sb.append(key + "=" + ConfigManager.ALL_PROPERTIES.get(key) + "\n");
            }
            File file = classPathResource.getFile();
            FileUtil.writeUseBio(file.getPath(), sb.toString());
        } catch (Exception e) {
            LOGGER.error("writeToLocaFile", "持久化配置信息到本地失败", e);
        }
    }

    private Map<String, String> loadPropertiesFromZookeeper() {
        Map<String, String> properties = new HashMap<String, String>();
        try {
            zk.makeSureConnected();
            loadPropertiesForPath(ZK.ZOOKEEPER_PATH_DELIITER, properties);

        } catch (Exception e) {
            LOGGER.error("loadPropertiesFromZookeeper", "从zookeeper加载配置信息失败", e);
        }
        return properties;
    }

    private void loadPropertiesForPath(String fullPath, Map<String, String> properties) throws Exception {
        List<String> subPaths = zk.getChildren(fullPath, getNodeWather());
        if (subPaths == null || subPaths.size() == 0) {
            // 只加载叶子结点
            byte[] data = zk.getData(fullPath, getNodeWather(), null);
            String propertyName = buildPropertyName(fullPath);
            String propertyValue = new String(data == null ? "" : new String(data));
            properties.put(propertyName, propertyValue);
        }
        for (String subPath : subPaths) {
            String fullSubPath = zk.getFullPath(fullPath, subPath);
            loadPropertiesForPath(fullSubPath, properties);
        }
    }

    private String buildPropertyName(String fullPath) {
        String propertyName = fullPath.replace(ZK.ZOOKEEPER_PATH_DELIITER, PROPERTY_DELIMITER);
        if (propertyName.startsWith(PROPERTY_DELIMITER)) {
            propertyName = propertyName.substring(1);
        }
        return propertyName;
    }

    /**
     * 启动从zookeeper读取配置信息
     */
    public synchronized void startLoadPropertiesFromZK() {
        try {
            if (zk != null && zk.isConnected()) {
                zk.register(buildConnectionStateWather());
                return;
            } else {
                initZKParams();
                String connectionString = hosts + ZK.ZOOKEEPER_PATH_DELIITER + CONFIG_ROOT;
                zk = new ZK(connectionString,
                        SESSION_TIMEOUT_MS,
                        buildConnectionStateWather(),
                        MAX_RETRIES,
                        BASE_SLEEP_TIMEMS);
            }
            Map<String, String> propertiesFromZookeeper = loadPropertiesFromZookeeper();
            ConfigManager.ALL_PROPERTIES.putAll(propertiesFromZookeeper);
            writeToLocalFile();
        } catch (Exception e) {
            LOGGER.error("startLoadPropertiesFromZK", "从zookeeper加载配置信息失败", e);
        }
    }

    private void initZKParams() {
        this.hosts = ConfigManager.ALL_PROPERTIES.get(ZOO_HOSTS_KEY);
    }

    private ConfigConnectionStateWatcher buildConnectionStateWather() {
        return new ConfigConnectionStateWatcher(this);
    }

    public ZooKeeper getZK() {
        return this.zk;
    }

    private Watcher getNodeWather() {
        return new ConfigNodeWatcher(this);
    }

    /**
     * 更新指定属性值
     *
     * @param path
     * @param propertyValue
     */
    private void updatePropertyByPath(String path, String propertyValue) {
        String propertyName = buildPropertyName(path);
        ConfigManager.ALL_PROPERTIES.put(propertyName, propertyValue);
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
        return ConfigManager.ALL_PROPERTIES.get(propertyName);
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

    /**
     * 重新读取zookeeper指定path下面的所有属性配置
     *
     * @param path
     */
    public synchronized void reloadChildPropertiesForPath(String path) {
        try {
            Map<String, String> newChidlrenProperties = new HashMap<String, String>();
            loadPropertiesForPath(path, newChidlrenProperties);
            String propertyName = buildPropertyName(path);
            deleteChildProperties(propertyName);
            if (newChidlrenProperties.size() > 0) {
                ConfigManager.ALL_PROPERTIES.putAll(newChidlrenProperties);
            }
            writeToLocalFile();
        } catch (Exception e) {
            LOGGER.error("reloadChildPropertiesForPath", "重新读取zookeeper指定path下面的所有属性配置失败", e);
        }
    }

    /**
     * 删除指定属性及其子属性
     *
     * @param path
     */
    public synchronized void deletePropertiesForPath(String path) {
        String propertyName = buildPropertyName(path);
        deleteChildProperties(propertyName);
        ConfigManager.ALL_PROPERTIES.remove(propertyName);
        writeToLocalFile();
    }

    /**
     * 重新从指定几点读取属性值
     *
     * @param path
     */
    public synchronized void reloadPropertyForPath(String path) {
        try {
            byte[] data = zk.getData(path, getNodeWather(), null);
            String value = (data == null ? "" : new String(data));
            updatePropertyByPath(path, value);
            writeToLocalFile();
        } catch (Throwable e) {
            LOGGER.error("reloadPropertyForPath", "重新从指定几点读取属性值失败", e);
        }
    }

    /**
     * 删除子属性
     *
     * @param propertyName
     */
    private void deleteChildProperties(String propertyName) {
        if (ConfigManager.ALL_PROPERTIES.contains(propertyName)) {
            String prefix = propertyName + PROPERTY_DELIMITER;
            List<String> keys = ListUtil.fromSet(ConfigManager.ALL_PROPERTIES.keySet());
            for (String key : keys) {
                if (key.toString().startsWith(prefix)) {
                    ConfigManager.ALL_PROPERTIES.remove(key);
                }
            }
        }
    }
}
