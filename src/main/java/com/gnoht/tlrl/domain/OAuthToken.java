package com.gnoht.tlrl.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="oauth_token")
public class OAuthToken extends Managed<Long, OAuthToken> {

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String token;
	private String secret;
	private String userIdentity;
	private String provider;

	public OAuthToken(String token, String secret, String provider) {
		this(token, secret, null, provider);
	}
	
	public OAuthToken(String token, String secret, String userIdentity, String provider) {
		this.token = token;
		this.secret = secret;
		this.userIdentity = userIdentity;
		this.provider = provider;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getUserIdentity() {
		return userIdentity;
	}
	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@Override
	public OAuthToken update(OAuthToken from) {
		return null;
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("token", token)
			.add("secret", "*")
			.add("userIdentity", userIdentity)
			.add("provider", provider);
	}
}
