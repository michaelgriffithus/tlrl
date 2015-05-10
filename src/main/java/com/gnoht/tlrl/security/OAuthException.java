package com.gnoht.tlrl.security;

import org.springframework.security.core.AuthenticationException;

public class OAuthException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct with generic message.
	 * @param t
	 */
	public OAuthException(Throwable t) {
		this("Unable to retrieve OAuth access token.", t);
	}
	
	public OAuthException(String msg, Throwable t) {
		super(msg, t);
	}
}
