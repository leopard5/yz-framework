package com.yz.framework.queue.converter;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import java.nio.charset.Charset;

public class FastJsonMessageConverter extends AbstractMessageConverter {
	private static Logger logger = LoggerFactory.getLogger(FastJsonMessageConverter.class);

	public final static Charset UTF8 = Charset.forName("UTF-8");

	private Charset charset = UTF8;

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public FastJsonMessageConverter() {
		super();
	}

	public Object fromMessage(Message message)
			throws MessageConversionException {
		logger.info(" public Object fromMessage(Message message) throws MessageConversionException :"
				+ message);
		return null;
	}

	public <T> T fromMessage(Message message, T t) {
		logger.info(" public Object fromMessage(Message message) throws MessageConversionException :"
				+ message);
		byte[] bytes = message.getBody();
		return JSON.parseObject(bytes, 0, bytes.length, charset.newDecoder(),
				t.getClass());
	}

	protected Message createMessage(Object objectToConvert,
			MessageProperties messageProperties)
			throws MessageConversionException {
		String jsonString = JSON.toJSONString(objectToConvert);
		byte[] bytes = jsonString.getBytes(this.getCharset());
		messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
		messageProperties.setContentEncoding(this.getCharset().name());
		if (bytes != null) {
			messageProperties.setContentLength(bytes.length);
		}
		return new Message(bytes, messageProperties);

	}
}
