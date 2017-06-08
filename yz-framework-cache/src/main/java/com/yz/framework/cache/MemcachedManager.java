/**
 * yz-framework-cache 
 * MemcacheClient.java 
 * com.yz.framework.cache 
 * TODO  
 * @author yazhong.qi
 * @date   2016年4月1日 下午5:17:32 
 * @version   1.0
 */

package com.yz.framework.cache;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.schooner.MemCached.MemcachedItem;
import com.schooner.MemCached.TransCoder;
import com.whalin.MemCached.MemCachedClient;

/**
 * ClassName:MemcacheClient <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年4月1日 下午5:17:32 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class MemcachedManager {

	private MemCachedClient client;

	public void setClient(MemCachedClient client) {
		this.client = client;
	}

	/**
	 * Checks to see if key exists in cache.
	 * 
	 * @param key
	 *            the key to look for
	 * @return true if key found in cache, false if not (or if cache is down)
	 */
	public boolean keyExists(String key) {
		return client.keyExists(key);
	}

	/**
	 * Deletes an object from cache given cache key.
	 * 
	 * @param key
	 *            the key to be removed
	 * @return <code>true</code>, if the data was deleted successfully
	 */
	public boolean delete(String key) {
		return client.delete(key);
	}

	/**
	 * Stores data on the server; only the key and the value are specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @return true, if the data was successfully stored
	 */
	public boolean set(String key, Object value) {
		return client.set(key, value);
	}

	/**
	 * Stores data on the server; only the key and the value are specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, if the data was successfully stored
	 */
	public boolean set(String key, Object value, Integer hashCode) {
		return client.set(key, value, hashCode);
	}

	/**
	 * Stores data on the server; the key, value, and an expiration time are
	 * specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param expiry
	 *            when to expire the record
	 * @return true, if the data was successfully stored
	 */
	public boolean set(String key, Object value, Date expiry) {
		return client.set(key, value, expiry);
	}

	/**
	 * Stores data on the server; the key, value, and an expiration time are
	 * specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param expiry
	 *            when to expire the record
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, if the data was successfully stored
	 */
	public boolean set(String key, Object value, Date expiry, Integer hashCode) {
		return client.set(key, value, expiry, hashCode);
	}

	/**
	 * Adds data to the server; only the key and the value are specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @return true, if the data was successfully stored
	 */
	public boolean add(String key, Object value) {
		return client.add(key, value);
	}

	/**
	 * Adds data to the server; the key, value, and an optional hashcode are
	 * passed in.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, if the data was successfully stored
	 */
	public boolean add(String key, Object value, Integer hashCode) {
		return client.add(key, value, hashCode);
	}

	/**
	 * Adds data to the server; the key, value, and an expiration time are
	 * specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param expiry
	 *            when to expire the record
	 * @return true, if the data was successfully stored
	 */
	public boolean add(String key, Object value, Date expiry) {
		return client.add(key, value, expiry);
	}

	/**
	 * Adds data to the server; the key, value, and an expiration time are
	 * specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param expiry
	 *            when to expire the record
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, if the data was successfully stored
	 */
	public boolean add(String key, Object value, Date expiry, Integer hashCode) {
		return client.add(key, value, expiry, hashCode);
	}

	/**
	 * Updates data on the server; only the key and the value are specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @return true, if the data was successfully stored
	 */
	public boolean replace(String key, Object value) {
		return client.replace(key, value);
	}

	/**
	 * Updates data on the server; only the key and the value and an optional
	 * hash are specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, if the data was successfully stored
	 */
	public boolean replace(String key, Object value, Integer hashCode) {
		return client.replace(key, value, hashCode);
	}

	/**
	 * Updates data on the server; the key, value, and an expiration time are
	 * specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param expiry
	 *            when to expire the record
	 * @return true, if the data was successfully stored
	 */
	public boolean replace(String key, Object value, Date expiry) {
		return client.replace(key, value, expiry);
	}

	/**
	 * Updates data on the server; the key, value, and an expiration time are
	 * specified.
	 * 
	 * @param key
	 *            key to store data under
	 * @param value
	 *            value to store
	 * @param expiry
	 *            when to expire the record
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true, if the data was successfully stored
	 */
	public boolean replace(String key, Object value, Date expiry, Integer hashCode) {
		return client.replace(key, value, expiry, hashCode);
	}

	/**
	 * Store a counter to memcached given a key
	 * 
	 * @param key
	 *            cache key
	 * @param counter
	 *            number to store
	 * @return true/false indicating success
	 */
	public boolean storeCounter(String key, Long counter) {
		return storeCounter(key, counter, null, null);
	}

	/**
	 * Store a counter to memcached given a key
	 * 
	 * @param key
	 *            cache key
	 * @param counter
	 *            number to store
	 * @param date
	 *            when to expire the record
	 * @return true/false indicating success
	 */
	public boolean storeCounter(String key, Long counter, Date date) {
		return storeCounter(key, counter, date, null);
	}

	/**
	 * Store a counter to memcached given a key
	 * 
	 * @param key
	 *            cache key
	 * @param counter
	 *            number to store
	 * @param date
	 *            when to expire the record
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true/false indicating success
	 */
	public boolean storeCounter(String key, Long counter, Date date, Integer hashCode) {
		return set(key, counter, date, hashCode);
	}

	/**
	 * Store a counter to memcached given a key
	 * 
	 * @param key
	 *            cache key
	 * @param counter
	 *            number to store
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return true/false indicating success
	 */
	public boolean storeCounter(String key, Long counter, Integer hashCode) {
		return storeCounter(key, counter, null, hashCode);
	}

	/**
	 * Returns value in counter at given key as long.
	 * 
	 * @param key
	 *            cache ket
	 * @return counter value or -1 if not found
	 */
	public long getCounter(String key) {
		return getCounter(key, null);
	}

	/**
	 * Returns value in counter at given key as long.
	 * 
	 * @param key
	 *            cache ket
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return counter value or -1 if not found
	 */
	public long getCounter(String key, Integer hashCode) {

		return client.getCounter(key, hashCode);
	}

	/**
	 * Thread safe way to initialize and increment a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @return value of incrementer
	 */
	public long addOrIncr(String key) {
		return client.addOrIncr(key);
	}

	/**
	 * Thread safe way to initialize and increment a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            value to set or increment by
	 * @return value of incrementer
	 */
	public long addOrIncr(String key, long inc) {
		return client.addOrIncr(key, inc);
	}

	/**
	 * Thread safe way to initialize and increment a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            value to set or increment by
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return value of incrementer
	 */
	public long addOrIncr(String key, long inc, Integer hashCode) {
		return client.addOrIncr(key, inc, hashCode);
	}

	/**
	 * Thread safe way to initialize and decrement a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @return value of incrementer
	 */
	public long addOrDecr(String key) {
		return client.addOrDecr(key);
	}

	/**
	 * Thread safe way to initialize and decrement a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            value to set or increment by
	 * @return value of incrementer
	 */
	public long addOrDecr(String key, long inc) {
		return client.addOrDecr(key, inc);
	}

	/**
	 * Thread safe way to initialize and decrement a counter.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            value to set or increment by
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return value of incrementer
	 */
	public long addOrDecr(String key, long inc, Integer hashCode) {
		return client.addOrDecr(key, inc, hashCode);
	}

	/**
	 * Increment the value at the specified key by 1, and then return it.<br>
	 * Please make sure setPrimitiveAsString=true if the key/value pair is
	 * stored with set command.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long incr(String key) {
		return client.incr(key);
	}

	/**
	 * Increment the value at the specified key by passed in val.<br>
	 * Please make sure setPrimitiveAsString=true if the key/value pair is
	 * stored with set command.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            how much to increment by
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long incr(String key, long inc) {
		return client.incr(key, inc);
	}

	/**
	 * Increment the value at the specified key by the specified increment, and
	 * then return it.<br>
	 * Please make sure setPrimitiveAsString=true if the key/value pair is
	 * stored with set command.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            how much to increment by
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long incr(String key, long inc, Integer hashCode) {
		return client.incr(key, inc, hashCode);
	}

	/**
	 * Decrement the value at the specified key by 1, and then return it.<br>
	 * Please make sure setPrimitiveAsString=true if the key/value pair is
	 * stored with set command.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long decr(String key) {
		return client.decr(key);
	}

	/**
	 * Decrement the value at the specified key by passed in value, and then
	 * return it.<br>
	 * Please make sure setPrimitiveAsString=true if the key/value pair is
	 * stored with set command.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            how much to increment by
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long decr(String key, long inc) {
		return client.decr(key, inc);
	}

	/**
	 * Decrement the value at the specified key by the specified increment, and
	 * then return it.<br>
	 * Please make sure setPrimitiveAsString=true if the key/value pair is
	 * stored with set command.
	 * 
	 * @param key
	 *            key where the data is stored
	 * @param inc
	 *            how much to increment by
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return -1, if the key is not found, the value after incrementing
	 *         otherwise
	 */
	public long decr(String key, long inc, Integer hashCode) {
		return client.decr(key, inc, hashCode);
	}

	/**
	 * Retrieve a key from the server, using a specific hash.
	 * 
	 * If the data was compressed or serialized when compressed, it will
	 * automatically<br/>
	 * be decompressed or serialized, as appropriate. (Inclusive or)<br/>
	 * <br/>
	 * Non-serialized data will be returned as a string, so explicit conversion
	 * to<br/>
	 * numeric types will be necessary, if desired<br/>
	 * 
	 * @param key
	 *            key where data is stored
	 * @return the object that was previously stored, or null if it was not
	 *         previously stored
	 */
	public Object get(String key) {
		return client.get(key);
	}

	/**
	 * Retrieve a key from the server, using a specific hash.
	 * 
	 * If the data was compressed or serialized when compressed, it will
	 * automatically<br/>
	 * be decompressed or serialized, as appropriate. (Inclusive or)<br/>
	 * <br/>
	 * Non-serialized data will be returned as a string, so explicit conversion
	 * to<br/>
	 * numeric types will be necessary, if desired<br/>
	 * 
	 * @param key
	 *            key where data is stored
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @return the object that was previously stored, or null if it was not
	 *         previously stored
	 */
	public Object get(String key, Integer hashCode) {
		return client.get(key, hashCode);
	}

	public MemcachedItem gets(String key) {
		return client.gets(key);
	}

	public MemcachedItem gets(String key, Integer hashCode) {
		return client.gets(key, hashCode);
	}

	public void setTransCoder(TransCoder transCoder) {
		client.setTransCoder(transCoder);
	}

	/**
	 * Retrieve a key from the server, using a specific hash.
	 * 
	 * If the data was compressed or serialized when compressed, it will
	 * automatically<br/>
	 * be decompressed or serialized, as appropriate. (Inclusive or)<br/>
	 * <br/>
	 * Non-serialized data will be returned as a string, so explicit conversion
	 * to<br/>
	 * numeric types will be necessary, if desired<br/>
	 * 
	 * @param key
	 *            key where data is stored
	 * @param hashCode
	 *            if not null, then the int hashcode to use
	 * @param asString
	 *            if true, then return string val
	 * @return the object that was previously stored, or null if it was not
	 *         previously stored
	 */
	public Object get(String key, Integer hashCode, boolean asString) {
		return client.get(key, hashCode, asString);
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @return Object array ordered in same order as key array containing
	 *         results
	 */
	public Object[] getMultiArray(String[] keys) {
		return client.getMultiArray(keys);
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @return Object array ordered in same order as key array containing
	 *         results
	 */
	public Object[] getMultiArray(String[] keys, Integer[] hashCodes) {
		return client.getMultiArray(keys, hashCodes);
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @param asString
	 *            if true, retrieve string vals
	 * @return Object array ordered in same order as key array containing
	 *         results
	 */
	public Object[] getMultiArray(String[] keys, Integer[] hashCodes, boolean asString) {
		return client.getMultiArray(keys, hashCodes, asString);
	}

	/**
	 * Retrieve multiple objects from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            String array of keys to retrieve
	 * @return a hashmap with entries for each key is found by the server, keys
	 *         that are not found are not entered into the hashmap, but
	 *         attempting to retrieve them from the hashmap gives you null.
	 */
	public Map<String, Object> getMulti(String[] keys) {
		return getMulti(keys, null);
	}

	/**
	 * Retrieve multiple keys from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @return a hashmap with entries for each key is found by the server, keys
	 *         that are not found are not entered into the hashmap, but
	 *         attempting to retrieve them from the hashmap gives you null.
	 */
	public Map<String, Object> getMulti(String[] keys, Integer[] hashCodes) {
		return client.getMulti(keys, hashCodes);
	}

	/**
	 * Retrieve multiple keys from the memcache.
	 * 
	 * This is recommended over repeated calls to {@link #get(String) get()},
	 * since it<br/>
	 * is more efficient.<br/>
	 * 
	 * @param keys
	 *            keys to retrieve
	 * @param hashCodes
	 *            if not null, then the Integer array of hashCodes
	 * @param asString
	 *            if true then retrieve using String val
	 * @return a hashmap with entries for each key is found by the server, keys
	 *         that are not found are not entered into the hashmap, but
	 *         attempting to retrieve them from the hashmap gives you null.
	 */
	public Map<String, Object> getMulti(String[] keys, Integer[] hashCodes, boolean asString) {
		return client.getMulti(keys, hashCodes, asString);
	}

	/**
	 * Invalidates the entire cache.
	 * 
	 * Will return true only if succeeds in clearing all servers.
	 * 
	 * @return success true/false
	 */
	public boolean flushAll() {
		return client.flushAll();
	}

	/**
	 * Invalidates the entire cache.
	 * 
	 * Will return true only if succeeds in clearing all servers. If pass in
	 * null, then will try to flush all servers.
	 * 
	 * @param servers
	 *            optional array of host(s) to flush (host:port)
	 * @return success true/false
	 */
	public boolean flushAll(String[] servers) {
		return client.flushAll(servers);
	}

	/**
	 * Retrieves stats for all servers.
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains stats with stat name as key and value as value.
	 * 
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> stats() {
		return client.stats();
	}

	/**
	 * Retrieves stats for passed in servers (or all servers).
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains stats with stat name as key and value as value.
	 * 
	 * @param servers
	 *            string array of servers to retrieve stats from, or all if this
	 *            is null
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> stats(String[] servers) {
		return client.stats(servers);
	}

	/**
	 * Retrieves stats items for all servers.
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains item stats with itemname:number:field as key and value as value.
	 * 
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> statsItems() {
		return client.statsItems();
	}

	/**
	 * Retrieves stats for passed in servers (or all servers).
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains item stats with itemname:number:field as key and value as value.
	 * 
	 * @param servers
	 *            string array of servers to retrieve stats from, or all if this
	 *            is null
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> statsItems(String[] servers) {
		return client.statsItems(servers);
	}

	/**
	 * Retrieves stats items for all servers.
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains slabs stats with slabnumber:field as key and value as value.
	 * 
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> statsSlabs() {
		return client.statsSlabs();
	}

	/**
	 * Retrieves stats for passed in servers (or all servers).
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains slabs stats with slabnumber:field as key and value as value.
	 * 
	 * @param servers
	 *            string array of servers to retrieve stats from, or all if this
	 *            is null
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> statsSlabs(String[] servers) {
		return client.statsSlabs(servers);
	}

	/**
	 * Retrieves items cachedump for all servers.
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains cachedump stats with the cachekey as key and byte size and unix
	 * timestamp as value.
	 * 
	 * @param slabNumber
	 *            the item number of the cache dump
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> statsCacheDump(int slabNumber, int limit) {
		return client.statsCacheDump(slabNumber, limit);
	}

	/**
	 * Retrieves stats for passed in servers (or all servers).
	 * 
	 * Returns a map keyed on the servername. The value is another map which
	 * contains cachedump stats with the cachekey as key and byte size and unix
	 * timestamp as value.
	 * 
	 * @param servers
	 *            string array of servers to retrieve stats from, or all if this
	 *            is null
	 * @param slabNumber
	 *            the item number of the cache dump
	 * @return Stats map
	 */
	public Map<String, Map<String, String>> statsCacheDump(String[] servers, int slabNumber, int limit) {
		return client.statsCacheDump(servers, slabNumber, limit);
	}

	public boolean sync(String key, Integer hashCode) {
		return client.sync(key, hashCode);
	}

	public boolean sync(String key) {
		return client.sync(key);
	}

	public boolean syncAll() {
		return client.syncAll();
	}

	public boolean syncAll(String[] servers) {
		return client.syncAll(servers);
	}

	public boolean append(String key, Object value, Integer hashCode) {
		return client.append(key, value, hashCode);
	}

	public boolean append(String key, Object value) {
		return client.append(key, value);
	}

	public boolean cas(String key, Object value, Integer hashCode, long casUnique) {
		return client.cas(key, value, hashCode, casUnique);
	}

	public boolean cas(String key, Object value, Date expiry, long casUnique) {
		return client.cas(key, value, expiry, casUnique);
	}

	public boolean cas(String key, Object value, Date expiry, Integer hashCode, long casUnique) {
		return client.cas(key, value, expiry, hashCode, casUnique);
	}

	public boolean cas(String key, Object value, long casUnique) {
		return client.cas(key, value, casUnique);
	}

	public boolean prepend(String key, Object value, Integer hashCode) {
		return client.prepend(key, value, hashCode);
	}

	public boolean prepend(String key, Object value) {
		return client.prepend(key, value);
	}

}
