/**
 * yz-framework-cache 
 * MemcacheTest.java 
 * com.yz.framework.cache.test 
 * TODO  
 * @author yazhong.qi
 * @date   2016年4月24日 下午7:27:23 
 * @version   1.0
 */

package com.yz.framework.cache.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import com.yz.framework.cache.MemcachedManager;

/**
 * ClassName:MemcacheTest <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年4月24日 下午7:27:23 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class MemcacheTest {
	@Test
	public void test() throws Exception {
		// SockIOPool pool = SockIOPool.getInstance("memcache", true);
		// String[] servers = new String[] { "192.168.1.104:11211" };
		// pool.setServers(servers);
		// pool.setHashingAlg(SockIOPool.NEW_COMPAT_HASH);
		// pool.initialize();
		//
		// MemcachedManager manager = new MemcachedManager();
		// MemCachedClient client = new MemCachedClient("memcache", true, true);
		// manager.setClient(client);
		// System.out.println(manager.add("hello", "world"));
		// System.out.println(manager.add("hello", "world2"));
		// System.out.println(manager.set("hello", "world2"));
		// // manager.set("hello", "yazhong.qi");
		// manager.set("foo", "bar");
		//
		// List<String> list = Arrays.asList("yazhong.qi", "1", "1", "1", "1");
		//
		// System.out.println(manager.set("aa", list));
		//
		// List<String> bb = get(manager);
		// for (String b : bb) {
		// System.out.println(b);
		// }

	}

	private <T> T get(MemcachedManager manager) {
		T bb = (T) manager.get("aa");
		return bb;
	}
}
