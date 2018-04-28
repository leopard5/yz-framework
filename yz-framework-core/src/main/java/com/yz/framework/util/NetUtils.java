package com.yz.framework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class NetUtils {
    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;

    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANYHOST = "0.0.0.0";

    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }

    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static int getAvailablePort(int port) {
        if (port <= 0) {
            return getAvailablePort();
        }
        for (int i = port; i < MAX_PORT; i++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(i);
                return i;
            } catch (IOException e) {
                // continue
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return port;
    }



    public static boolean isInvalidPort(int port) {
        return port > MIN_PORT && port <= MAX_PORT;
    }

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");

    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    public static boolean isLocalHost(String host) {
        return host != null
                && (LOCAL_IP_PATTERN.matcher(host).matches()
                || host.equalsIgnoreCase("localhost"));
    }

    public static boolean isAnyHost(String host) {
        return "0.0.0.0".equals(host);
    }

    public static boolean isInvalidLocalHost(String host) {
        return host == null
                || host.length() == 0
                || host.equalsIgnoreCase("localhost")
                || host.equals("0.0.0.0")
                || (LOCAL_IP_PATTERN.matcher(host).matches());
    }

    public static boolean isValidLocalHost(String host) {
        return !isInvalidLocalHost(host);
    }

    public static InetSocketAddress getLocalSocketAddress(String host, int port) {
        return isInvalidLocalHost(host) ?
                new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    public static String getLogHost() {
        InetAddress address = LOCAL_ADDRESS;
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retriving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }


    /**
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

    public static String toAddressString(InetSocketAddress address) {
        return address.getAddress().getHostAddress() + ":" + address.getPort();
    }

    public static InetSocketAddress toAddress(String address) {
        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }

    public static String toURL(String protocol, String host, int port, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append("://");
        sb.append(host).append(':').append(port);
        if (path.charAt(0) != '/')
            sb.append('/');
        sb.append(path);
        return sb.toString();
    }

    /**
     * slf4j Logger for this class
     */
    private final static Logger LOGGER   = LoggerFactory.getLogger(NetUtils.class);

    /**
     * 最小端口
     */
    private static final int    MIN_PORT = 0;
    /**
     * 最大端口
     */
    private static final int    MAX_PORT = 65535;

    /**
     * 判断端口是否有效 0-65535
     *
     * @param port 端口
     * @return 是否有效
     */
    public static boolean isInvalidPort(int port) {
        return port > MAX_PORT || port < MIN_PORT;
    }

    /**
     * 判断端口是否随机端口 小于0表示随机
     *
     * @param port 端口
     * @return 是否随机端口
     */
    public static boolean isRandomPort(int port) {
        return port == -1;
    }

    /**
     * 检查当前指定端口是否可用，不可用则自动+1再试（随机端口从默认端口开始检查）
     *
     * @param host 当前ip地址
     * @param port 当前指定端口
     * @return 从指定端口开始后第一个可用的端口
     */
    public static int getAvailablePort(String host, int port) {
        return getAvailablePort(host, port, MAX_PORT);
    }

    /**
     * 检查当前指定端口是否可用，不可用则自动+1再试（随机端口从默认端口开始检查）
     *
     * @param host    当前ip地址
     * @param port    当前指定端口
     * @param maxPort 最大端口
     * @return 从指定端口开始后第一个可用的端口
     */
    public static int getAvailablePort(String host, int port, int maxPort) {
        if (isAnyHost(host)
                || isLocalHost(host)
                || isHostInNetworkCard(host)) {
            if (port < MIN_PORT) {
                port = MIN_PORT;
            }
            for (int i = port; i <= maxPort; i++) {
                ServerSocket ss = null;
                try {
                    ss = new ServerSocket();
                    ss.bind(new InetSocketAddress(host, i));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("ip:{} port:{} is available", host, i);
                    }
                    return i;
                } catch (IOException e) {
                    // continue
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Can't bind to address [{}:{}], " +
                                "Maybe 1) The port has been bound. " +
                                "2) The network card of this host is not exists or disable. " +
                                "3) The host is wrong.", host, i);
                    }
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("Begin try next port(auto +1):{}", i + 1);
                    }
                } finally {
                    IOUtils.closeQuietly(ss);
                }
            }
            throw new SofaRpcRuntimeException("Can't bind to ANY port of " + host + ", please check config");
        } else {
            throw new SofaRpcRuntimeException("The host " + host
                    + " is not found in network cards, please check config");
        }
    }

    /**
     * 任意地址
     */
    public static final String   ANYHOST          = "0.0.0.0";
    /**
     * 本机地址正则
     */
    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");

    /**
     * IPv4地址
     */
    public static final Pattern  IPV4_PATTERN     = Pattern
            .compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    /**
     * 是否本地地址 127.x.x.x 或者 localhost
     *
     * @param host 地址
     * @return 是否本地地址
     */
    public static boolean isLocalHost(String host) {
        return StringUtils.isNotBlank(host)
                && (LOCAL_IP_PATTERN.matcher(host).matches() || "localhost".equalsIgnoreCase(host));
    }

    /**
     * 是否默认地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isAnyHost(String host) {
        return ANYHOST.equals(host);
    }

    /**
     * 是否IPv4地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isIPv4Host(String host) {
        return StringUtils.isNotBlank(host)
                && IPV4_PATTERN.matcher(host).matches();
    }

    /**
     * 是否非法地址（本地或默认）
     *
     * @param host 地址
     * @return 是否非法地址
     */
    static boolean isInvalidLocalHost(String host) {
        return StringUtils.isBlank(host)
                || isAnyHost(host)
                || isLocalHost(host);
    }

    /**
     * 是否合法地址（非本地，非默认的IPv4地址）
     *
     * @param address InetAddress
     * @return 是否合法
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !isAnyHost(name)
                && !isLocalHost(name)
                && isIPv4Host(name));
    }

    /**
     * 是否网卡上的地址
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isHostInNetworkCard(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 得到本机IPv4地址
     *
     * @return ip地址
     */
    public static String getLocalIpv4() {
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP，保存到缓存中
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
            }
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                                if (LOGGER.isWarnEnabled()) {
                                    LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        if (LOGGER.isWarnEnabled()) {
                            LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error when retrieving ip address: " + e.getMessage(), e);
            }
        }
        if (LOGGER.isErrorEnabled()) {
            LOGGER.error("Can't get valid host, will use 127.0.0.1 instead.");
        }
        return localAddress;
    }

    /**
     * InetSocketAddress转 host:port 字符串
     *
     * @param address InetSocketAddress转
     * @return host:port 字符串
     */
    public static String toAddressString(InetSocketAddress address) {
        if (address == null) {
            return StringUtils.EMPTY;
        } else {
            return toIpString(address) + ":" + address.getPort();
        }
    }

    /**
     * 得到ip地址
     *
     * @param address InetSocketAddress
     * @return ip地址
     */
    public static String toIpString(InetSocketAddress address) {
        if (address == null) {
            return null;
        } else {
            InetAddress inetAddress = address.getAddress();
            return inetAddress == null ? address.getHostName() :
                    inetAddress.getHostAddress();
        }
    }

    /**
     * 本地多ip情况下、连一下注册中心地址得到本地IP地址
     *
     * @param registryIp 注册中心地址
     * @return 本地多ip情况下得到本地能连上注册中心的IP地址
     */
    public static String getLocalHostByRegistry(String registryIp) {
        String host = null;
        if (registryIp != null && registryIp.length() > 0) {
            List<InetSocketAddress> addrs = getIpListByRegistry(registryIp);
            for (int i = 0; i < addrs.size(); i++) {
                InetAddress address = getLocalHostBySocket(addrs.get(i));
                if (address != null) {
                    host = address.getHostAddress();
                    if (host != null && !NetUtils.isInvalidLocalHost(host)) {
                        return host;
                    }
                }
            }
        }
        if (NetUtils.isInvalidLocalHost(host)) {
            host = NetUtils.getLocalIpv4();
        }
        return host;
    }

    /**
     * 通过连接远程地址得到本机内网地址
     *
     * @param remoteAddress 远程地址
     * @return 本机内网地址
     */
    private static InetAddress getLocalHostBySocket(InetSocketAddress remoteAddress) {
        InetAddress host = null;
        try {
            // 去连一下远程地址
            Socket socket = new Socket();
            try {
                socket.connect(remoteAddress, 1000);
                // 得到本地地址
                host = socket.getLocalAddress();
            } finally {
                IOUtils.closeQuietly(socket);
            }
        } catch (Exception e) {
            LOGGER.warn("Can not connect to host {}, cause by :{}",
                    remoteAddress.toString(), e.getMessage());
        }
        return host;
    }

    /**
     * 解析注册中心地址配置为多个连接地址
     *
     * @param registryIp 注册中心地址
     * @return 可以连接注册中心的本机IP
     */
    public static List<InetSocketAddress> getIpListByRegistry(String registryIp) {
        List<String[]> ips = new ArrayList<String[]>();
        String defaultPort = null;

        String[] srcIps = registryIp.split(",");
        for (String add : srcIps) {
            int a = add.indexOf("://");
            if (a > -1) {
                add = add.substring(a + 3); // 去掉协议头
            }
            String[] s1 = add.split(":");
            if (s1.length > 1) {
                if (defaultPort == null && s1[1] != null && s1[1].length() > 0) {
                    defaultPort = s1[1];
                }
                ips.add(new String[] { s1[0], s1[1] }); // 得到ip和端口
            } else {
                ips.add(new String[] { s1[0], defaultPort });
            }
        }

        List<InetSocketAddress> ads = new ArrayList<InetSocketAddress>();
        for (int j = 0; j < ips.size(); j++) {
            String[] ip = ips.get(j);
            try {
                InetSocketAddress address = new InetSocketAddress(ip[0],
                        Integer.parseInt(ip[1] == null ? defaultPort : ip[1]));
                ads.add(address);
            } catch (Exception ignore) { //NOPMD
            }
        }

        return ads;
    }

    /**
     * 判断当前ip是否符合白名单
     *
     * @param whiteList 白名单，可以配置为*
     * @param localIP   当前地址
     * @return 是否在名单里
     */
    public static boolean isMatchIPByPattern(String whiteList, String localIP) {
        if (StringUtils.isNotBlank(whiteList)) {
            if (StringUtils.ALL.equals(whiteList)) {
                return true;
            }
            for (String ips : whiteList.replace(',', ';').split(";", -1)) {
                try {
                    if (ips.contains(StringUtils.ALL)) { // 带通配符
                        String regex = ips.trim().replace(".", "\\.").replace("*", ".*");
                        Pattern pattern = Pattern.compile(regex);
                        if (pattern.matcher(localIP).find()) {
                            return true;
                        }
                    } else if (!isIPv4Host(ips)) { // 不带通配符的正则表达式
                        String regex = ips.trim().replace(".", "\\.");
                        Pattern pattern = Pattern.compile(regex);
                        if (pattern.matcher(localIP).find()) {
                            return true;
                        }
                    } else {
                        if (ips.equals(localIP)) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    LOGGER.warn("syntax of pattern {} is invalid", ips);
                }
            }
        }
        return false;
    }

    /**
     * 连接转字符串
     *
     * @param local  本地地址
     * @param remote 远程地址
     * @return 地址信息字符串
     */
    public static String connectToString(InetSocketAddress local, InetSocketAddress remote) {
        return toAddressString(local) + " <-> " + toAddressString(remote);
    }

    /**
     * 连接转字符串
     *
     * @param local1  本地地址
     * @param remote1 远程地址
     * @return 地址信息字符串
     */
    public static String channelToString(SocketAddress local1, SocketAddress remote1) {
        try {
            InetSocketAddress local = (InetSocketAddress) local1;
            InetSocketAddress remote = (InetSocketAddress) remote1;
            return toAddressString(local) + " -> " + toAddressString(remote);
        } catch (Exception e) {
            return local1 + "->" + remote1;
        }
    }

    /**
     * 是否可以telnet
     *
     * @param ip      远程地址
     * @param port    远程端口
     * @param timeout 连接超时
     * @return 是否可连接
     */
    public static boolean canTelnet(String ip, int port, int timeout) {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            return socket.isConnected() && !socket.isClosed();
        } catch (Exception e) {
            return false;
        } finally {
            IOUtils.closeQuietly(socket);
        }
    }
}
