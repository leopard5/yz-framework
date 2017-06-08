/**
 * yz-framework-security 
 * HexUtil.java 
 * com.yz.framework.security 
 * TODO  
 * @author yazhong.qi
 * @date   2016年5月13日 上午11:03:47 
 * @version   1.0
 */

package com.yz.framework.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * ClassName:HexUtil <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年5月13日 上午11:03:47 <br/>
 * 
 * @author yazhong.qi
 * @version 1.0
 * @since JDK 1.7
 * @see
 */
public abstract class HexUtil {

	public static String hex2Base64(String hex) throws DecoderException {
		byte[] data = hex2Bytes(hex);
		return Base64Util.encodeBase64String(data);

	}

	public static byte[] hex2Bytes(String hex) throws DecoderException {
		char[] data = hex.toCharArray();
		return Hex.decodeHex(data);
	}
}
