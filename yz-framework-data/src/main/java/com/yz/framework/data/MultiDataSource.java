package com.yz.framework.data;

import com.yz.framework.logging.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiDataSource extends AbstractRoutingDataSource {

	private final static Logger LOGGER = Logger.getLogger(MultiDataSource.class);

	// private int pre = -1;
	private int maxIndex;
	private final AtomicInteger preIndex = new AtomicInteger(0);

	private Map<Integer, Object> indexedLookupKey = new HashMap<Integer, Object>();

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		maxIndex = targetDataSources.size() - 1;
		Set<Object> keys = targetDataSources.keySet();
		int index = 1;
		for (Object object : keys) {
			indexedLookupKey.put(index, object);
			index++;
		}
	}

	@Override
	protected Object determineCurrentLookupKey() {
		if (maxIndex == 0) {
			return indexedLookupKey.get(0);
		}
		//
		// int val = pre.getAndIncrement();
		// int j = val % count;
		// System.out.println(j);
		// return indexedLookupKey.get(j);

		int i = preIndex.getAndIncrement();
		if (i > maxIndex) {
			preIndex.set(0);
			i = 0;
		}
		return indexedLookupKey.get(i);

	}
}
