package com.gnoht.tlrl.controller.support;

public interface ApiResponse<T> {
	String getUri();
	T getData();
}
