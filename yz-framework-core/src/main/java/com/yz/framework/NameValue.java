/**
 * yz-framework-core 
 * NameValue.java 
 * com.yz.framework 
 * TODO  
 * @author yazhong.qi
 * @date   2016年3月18日 上午11:01:18 
 * @version   1.0
 */

package com.yz.framework;

/**
 * ClassName:NameValue <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年3月18日 上午11:01:18 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public class NameValue<N, V> {
	private N name;
	private V value;

	public NameValue()
	{

	}

	public NameValue(N k, V v)
	{
		this.setName(k);
		this.setValue(v);
	}

	public N getName() {
		return name;
	}

	public void setName(N name) {
		this.name = name;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
