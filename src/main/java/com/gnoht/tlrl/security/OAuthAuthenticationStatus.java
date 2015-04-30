package com.gnoht.tlrl.security;

import java.io.Serializable;

public class OAuthAuthenticationStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum Code {
		SUCCESS("success"),
		FAILURE("failure"),
		ERROR("error");
		
		private String name;
		
		private Code(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
	}
	
	private Code code;
	private String message;

	/**
	 * Constructs {@link OAuthAuthenticationStatus} with SUCCESS code.
	 */
	public OAuthAuthenticationStatus() {
		this(Code.SUCCESS);
	}
	/**
	 * Constructs {@link OAuthAuthenticationStatus} with SUCCESS 
	 * code and given message.
	 * @param message
	 */
	public OAuthAuthenticationStatus(String message) {
		this(Code.SUCCESS, message);
	}
	/**
	 * Constructs {@link OAuthAuthenticationStatus} with given code.
	 * @param code
	 */
	public OAuthAuthenticationStatus(Code code) {
		this.code = code;
	}
	/**
	 * Constructs {@link OAuthAuthenticationStatus} with given
	 * code and message parameters.
	 * @param code
	 * @param message
	 */
	public OAuthAuthenticationStatus(Code code, String message) {
		this(code);
		setMessage(message);
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public boolean is(Code code) {
		return this.code == code;
	}
	
	@Override
	public String toString() {
		return "OAuthAuthenticationStatus [code=" + code + ", message=" + message
				+ "]";
	}
}
