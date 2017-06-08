package com.yz.framework.http;

import org.springframework.util.AntPathMatcher;

public class CaseInsensitiveAntPathMatcher extends AntPathMatcher {
	@Override
	public boolean match(String pattern, String path) {
		return super.match(pattern.toUpperCase(), path.toUpperCase());
	}
}
