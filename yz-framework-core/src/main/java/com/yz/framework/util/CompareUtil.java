/**
 * yz-framework-core 
 * CompareUtil.java 
 * com.yz.framework.util 
 * TODO  
 * @author yazhong.qi
 * @date   2016年3月16日 下午4:42:38 
 * @version   1.0
 */

package com.yz.framework.util;

/**
 * ClassName:CompareUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月16日 下午4:42:38 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class CompareUtil {

	public static <T extends Comparable<T>> boolean equal(T a, T b) {
		if (a == null || b == null) {
			return b == null && a == null;
		}
		return a.compareTo(b) == 0;
	}

}
