package com.yz.framework.security;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.DigestUtils;

import com.yz.framework.email.EmailSender;
import com.yz.framework.logging.Logger;

public class ApiRequestDataHandler implements InitializingBean {
	private static final Logger logger = Logger.getLogger(EmailSender.class);
	private String md5Key;
	private String md5KeyPre64;
	private String md5KeyBack64;
	private String apiKey = "api";
	private String messageKey = "message";
	private String fromKey = "from";
	private String signKey = "sign";
	private String characterEncoding = "utf-8";

	private List<String> supportFroms;

	public List<String> getSupportFroms() {
		return supportFroms;
	}

	public void setSupportFroms(List<String> supportFroms) {
		this.supportFroms = supportFroms;
	}

	public String getMd5Key() {
		return md5Key;
	}

	public void setMd5Key(String md5Key) {
		this.md5Key = md5Key;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getFromKey() {
		return fromKey;
	}

	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public String getRequestDataFormat() {
		return requestDataFormat;
	}

	public void setRequestDataFormat(String requestDataFormat) {
		this.requestDataFormat = requestDataFormat;
	}

	private String requestDataFormat = "api=%s&message=%s&from=%s&sign=%s";

	public ApiRequestDataHandler() {
		// do nothing
	}

	public ApiRequestDataHandler(String md5Key) throws Exception {
		if (md5Key.length() != 128) {
			throw new Exception("the length of md5Key must be 128.");
		}
		this.md5Key = md5Key;
		init();

	}

	public ApiRequestDataHandler(String md5Key, String apiKey, String messageKey, String fromKey, String signKey)
			throws Exception {
		this.md5Key = md5Key;
		this.messageKey = messageKey;
		this.apiKey = apiKey;
		this.fromKey = fromKey;
		this.signKey = signKey;
		init();
	}

	public String generateSign(String message) {
		String sign = "";
		String a1 = message + md5KeyPre64;
		try {
			a1 = DigestUtils.md5DigestAsHex(a1.getBytes(characterEncoding));
			String a2 = a1 + md5KeyBack64;
			sign = DigestUtils.md5DigestAsHex(a2.getBytes(characterEncoding)).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			logger.error("generateSign", "message["+ message + "]" ,e);
		}
		return sign;
	}

	public String getRequestData(String api, String message, String from) {
		try {
			return getRequestData(api, message, from, characterEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map<String, String> getRequestData(String bodyRawData) {
		Map<String, String> data = new HashMap<String, String>();
		String[] ssStrings = bodyRawData.split("&");
		for (String string : ssStrings) {
			String[] keyValue = string.split("=");
			data.put(keyValue[0], keyValue[1]);
		}
		// String message = data.get(messageKey);
		// String decodeMessage = docode(message);
		// data.put(messageKey, decodeMessage)
		return data;
	}

	public String decode(String message) {
		try {
			return decode(message, characterEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return message;
	}

	public String decode(String message, String encode) throws UnsupportedEncodingException {

		return URLDecoder.decode(message, encode);

	}

	public String getRequestData(String api, String message, String from, String encode)
			throws UnsupportedEncodingException {
		String sign = this.generateSign(message);
		String data = String.format(requestDataFormat, api, message, from, sign);
		return data;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();

	}

	private void init() {
		md5KeyPre64 = md5Key.substring(0, 64);
		System.out.println(md5KeyPre64);
		md5KeyBack64 = md5Key.substring(64);
		System.out.println(md5KeyBack64);
		requestDataFormat = requestDataFormat.replace("api", this.apiKey).replace("message", this.messageKey)
				.replace("from", this.fromKey).replace("sign", this.signKey);
	}

	public boolean isValidSign(String message, String sign) {
		String mySign = generateSign(message);
		return mySign.equalsIgnoreCase(sign);
	}

	public boolean isValidFrom(String from) {
		for (String supportFrom : supportFroms) {
			if (supportFrom.equalsIgnoreCase(from)) {
				return true;
			}
		}
		return false;
	}

}
