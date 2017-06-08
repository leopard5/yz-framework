package com.yz.framework.queue;

import java.util.List;
import java.util.Map;

public interface QueueManager {

	void stop();

	void start();

	boolean isConnectted();

	boolean delete(long JobMessageId);

	JobMessage peek(long JobMessageId);

	public JobMessage peekReady();

	public JobMessage peekDelayed();

	public JobMessage peekBuried();

	public int kick(int count);

	public Map<String, String> statsJobMessage(String JobMessageId);

	public Map<String, String> statsTube(String tubeName);

	public Map<String, String> stats();

	public List<String> listTubes();

	public String listTubeUsed();

}
