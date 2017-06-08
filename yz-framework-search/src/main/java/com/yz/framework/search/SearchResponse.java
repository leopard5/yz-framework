/**
 * yz-framework-search 
 * SearchData.java 
 * com.yz.framework.search 
 * TODO  
 * @author yazhong.qi
 * @date   2015年12月15日 下午3:12:50 
 * @version   1.0
 */

package com.yz.framework.search;

import java.util.List;
import java.util.Map;

/**
 * ClassName:SearchData <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015年12月15日 下午3:12:50 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class SearchResponse {
	private int numFound;
	private int start;
	private List<Map<String, Object>> docs;

	public int getNumFound() {
		return numFound;
	}

	public void setNumFound(int numFound) {
		this.numFound = numFound;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public List<Map<String, Object>> getDocs() {
		return docs;
	}

	public void setDocs(List<Map<String, Object>> docs) {
		this.docs = docs;
	}
}
