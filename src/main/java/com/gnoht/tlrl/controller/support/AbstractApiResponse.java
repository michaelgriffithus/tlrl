package com.gnoht.tlrl.controller.support;

public abstract class AbstractApiResponse<T> implements ApiResponse<T> {

	private String uri;
	
	public AbstractApiResponse(String uri) {
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
}
