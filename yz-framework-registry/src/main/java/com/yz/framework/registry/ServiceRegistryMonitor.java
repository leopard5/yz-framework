package com.yz.framework.registry;

import com.yz.framework.logging.Logger;
import com.yz.framework.zookeeper.ZK;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.util.List;

public class ServiceRegistryMonitor implements BeanFactoryPostProcessor, SmartApplicationListener {

    private final static Logger LOGGER = Logger.getLogger(ServiceRegistryMonitor.class);
    private String appName;
    private int baseSleepTimeMs;
    private ZK zk;
    private String hosts;
    private boolean isSysToServer = false;
    private int maxRetries;
    private final String namespace = "registry";
    private ServiceRegistry serviceRegistry;
    private boolean useZookeeper;

    private int sessionTimeOutMs;

    public String getAppName() {
        return appName;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }

    public String getHosts() {
        return hosts;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    public int getSessionTimeOutMs() {
        return sessionTimeOutMs;
    }

    public void notifyRegistryServiceStop() {
        try {
            zk.makeSureConnected();
            List<String> services = serviceRegistry.getServices();
            for (String service : services) {
                zk.setData(service, "0");
            }
        } catch (Exception e) {
            LOGGER.error("notifyRegistryServiceStop", "notify registry service stop failed", e);
        }

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.serviceRegistry = new ServiceRegistry(this.appName);
        ServiceBeanProcessor ServiceBeanProcessor = new ServiceBeanProcessor(serviceRegistry);
        beanFactory.addBeanPostProcessor(ServiceBeanProcessor);
        LOGGER.info("postProcessBeanFactory", "add ServiceBeanProcessor");
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setBaseSleepTimeMs(int baseSleepTimeMs) {
        this.baseSleepTimeMs = baseSleepTimeMs;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public void setServiceRegistry(final ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    public void setSessionTimeOutMs(int sessionTimeOutMs) {
        this.sessionTimeOutMs = sessionTimeOutMs;
    }

    private void start() throws Exception {
        if (useZookeeper) {
            String connectionString = hosts + ZK.ZOOKEEPER_PATH_DELIITER + namespace;
            zk = new ZK(connectionString, sessionTimeOutMs, new RegistryWatcher(zk), maxRetries, baseSleepTimeMs);
            synToRegistry();
        }
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {

        return eventType == ContextRefreshedEvent.class || eventType == ContextStoppedEvent.class
                || eventType == ContextClosedEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ContextRefreshedEvent) {
            try {
                start();
            } catch (Exception e) {
                LOGGER.error("onApplicationEvent", "syn to registry failed.", e);
            }
        }
        if (event instanceof ContextStoppedEvent || event instanceof ContextClosedEvent) {
            if (zk != null) {
                notifyRegistryServiceStop();
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    LOGGER.info("onApplicationEvent", "close zookeeper connection failed!", e);
                }
                LOGGER.info("onApplicationEvent", "service " + appName + " is stoped");
            }
        }
    }

    public synchronized void synToRegistry() {
        if (isSysToServer) {
            return;
        }
        try {
            List<String> services = serviceRegistry.getServices();
            zk.makeSureConnected();
            if (!zk.isConnected()) {
                LOGGER.error("synToRegistry", "Registry Server is not connected.");
                return;
            }
            for (String service : services) {
                if (!zk.exists(service)) {
                    zk.createParentNodeIfExists(service, "1".getBytes());
                }
            }
            isSysToServer = true;
        } catch (Exception e) {
            LOGGER.error("synToRegistry", "synToRegistry failed", e);
        }
    }

    public ApiServiceObject getApiServiceObject(String api) {
        return serviceRegistry.getApiServiceObject(api);
    }

    public boolean isUseZookeeper() {
        return useZookeeper;
    }

    public void setUseZookeeper(boolean useZookeeper) {
        this.useZookeeper = useZookeeper;
    }
}
