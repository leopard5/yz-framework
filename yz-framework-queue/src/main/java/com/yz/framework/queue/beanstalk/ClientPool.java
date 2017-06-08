package com.yz.framework.queue.beanstalk;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.yz.framework.logging.Logger;
import com.yz.framework.util.ListUtil;

public class ClientPool {
	private static final Logger logger = Logger.getLogger(ClientPool.class);
	private static final Object lock = new Object();
	private List<ClientConnection> allConnections;
	private List<ClientConnection> aliveConnections;
	private Set<ClientConnection> deadConnections;
	private volatile int pre = -1;
	private volatile boolean startted;

	public static final String HOSTS_DELIMITER = ",";
	public static final String HOST_IP_DELIMITER = ":";
	private final Timer timer = new Timer("check queue connection", true);
	private final TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {

			synchronized (aliveConnections) {
				if (deadConnections.isEmpty()) {
					return;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("run check connection timer task ");
				}
				List<ClientConnection> aliveConns = new ArrayList<ClientConnection>(allConnections.size());
				for (ClientConnection clientConnection : deadConnections) {
					if (clientConnection.connect()) {
						aliveConns.add(clientConnection);
						logger.debug("the queue come back," + clientConnection.getHost() + ":" + clientConnection.getPort());
					}
				}
				if (aliveConns.size() > 0) {
					deadConnections.removeAll(aliveConns);
					aliveConnections = ListUtil.combine(aliveConnections, aliveConns);
				}
			}
		}
	};

	public ClientPool(
			String hosts)
	{
		this(hosts, null);
	}

	public ClientPool(
			String hosts, String[] watchTubeNames)
	{
		String[] hostArray = hosts.split(HOSTS_DELIMITER);
		allConnections = new ArrayList<ClientConnection>(hostArray.length);

		for (String host_port : hostArray) {
			if (host_port.trim().length() == 0) {
				continue;
			}
			String[] hp = host_port.trim().split(HOST_IP_DELIMITER);
			if (hp.length == 2) {
				ClientConnection connection = new ClientConnection(hp[0], Integer.parseInt(hp[1]), watchTubeNames);
				allConnections.add(connection);
			}
		}
	}

	public void start() {
		aliveConnections = new ArrayList<ClientConnection>(allConnections.size());
		deadConnections = new HashSet<ClientConnection>();
		for (ClientConnection connection : allConnections) {
			if (connection.connect()) {
				aliveConnections.add(connection);
			}
			else {
				deadConnections.add(connection);
			}
		}
		timer.schedule(timerTask, 30000, 30000);
		startted = true;
	}

	public boolean isStartted() {
		return startted;
	}

	public ClientConnection getConnection() {
		ClientConnection connection = null;
		int count = aliveConnections.size();
		if (count == 0) {
			return connection;
		}
		if (count == 1) {
			try {
				connection = aliveConnections.get(0);
				if (logger.isDebugEnabled()) {
					logger.debug("get queue " + connection.getHost() + ":" + connection.getPort());
				}
				return connection;
			} catch (IndexOutOfBoundsException e) {
				logger.debug("high conconurey");
				return null;
			}
		}
		int i = pre, j = pre;
		do {
			j = (j + 1) % count;
		} while (i == j);
		pre = j;
		try {
			connection = aliveConnections.get(j);
			if (logger.isDebugEnabled()) {
				logger.debug("get queue " + connection.getHost() + ":" + connection.getPort());
			}
			return connection;
		} catch (IndexOutOfBoundsException e) {
			logger.info("high conconurey");
			return getConnection();
		}
	}

	public void close() {
		if (!startted) {
			return;
		}
		synchronized (lock) {
			if (!startted) {
				return;
			}
			startted = false;
			timer.cancel();
			for (ClientConnection clientConnection : aliveConnections) {
				clientConnection.close();
			}
			aliveConnections = null;
		}
	}

	public boolean hasAliveConnection() {
		return aliveConnections.size() > 0;
	}

	public int getAliveCount() {
		return aliveConnections.size();
	}

	public List<ClientConnection> getAliveConnections() {
		return aliveConnections;
	}

	public List<ClientConnection> getAllConnections() {
		return allConnections;
	}

	public void checkConnection(ClientConnection connection) {
		synchronized (aliveConnections) {
			if (!connection.checkIsConnected()) {
				if (aliveConnections.contains(connection)) {
					aliveConnections.remove(connection);
					deadConnections.add(connection);
				}
			}
		}
	}
}
