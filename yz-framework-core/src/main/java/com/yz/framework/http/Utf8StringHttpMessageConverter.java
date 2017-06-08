package com.yz.framework.http;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.converter.StringHttpMessageConverter;

public class Utf8StringHttpMessageConverter extends StringHttpMessageConverter {
	private final List<Charset> availableCharsets;

	public Utf8StringHttpMessageConverter() {
		super(StandardCharsets.UTF_8);
		availableCharsets = new ArrayList<Charset>();
		availableCharsets.add(StandardCharsets.UTF_8);
	}

	@Override
	protected List<Charset> getAcceptedCharsets() {
		return availableCharsets;
	}
}
