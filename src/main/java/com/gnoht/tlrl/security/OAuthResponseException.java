package com.gnoht.tlrl.security;

import org.scribe.exceptions.OAuthException;

public class OAuthResponseException extends OAuthException {

	private static final long serialVersionUID = 1L;
	
	private final String response;
	
	public OAuthResponseException(String message, Exception e, String response) {
		super(message, e);
		this.response = response;
	}
	public OAuthResponseException(String message, String response) {
		this(message, null, response);
	}
	
	public OAuthResponseException(String message, Exception e) {
		this(message, e, null);
	}
	
	public OAuthResponseException(String message) {
		this(message, null, null);
	}

	public String getResponse() {
		return response;
	}
	
}
