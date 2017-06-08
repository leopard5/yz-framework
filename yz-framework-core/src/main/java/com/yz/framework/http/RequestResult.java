package com.yz.framework.http;

public class RequestResult {

	private String reasonPhrase;
	private int status;
	private String body;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}

	public boolean success() {
		return status == 200 || status == 304;
	}

}
