package com.gnoht.tlrl.security;

import org.springframework.security.oauth2.client.OAuth2RestOperations;

public abstract class OAuth2AuthenticationTokenServiceBuilder {
	
	protected OAuth2RestOperations restTemplate;
	protected String baseAuthorizationUrl;
	protected String clientId;
	protected String clientSecret;
	protected String tokenName;
	protected OAuth2AuthenticationTokenConverter converter;
	
	public OAuth2AuthenticationTokenServiceBuilder(){}
	
	public OAuth2AuthenticationTokenServiceBuilder(OAuth2RestOperations restTemplate){
		this.restTemplate = restTemplate;
	}
	
	public OAuth2AuthenticationTokenServiceBuilder restTemplate(
			OAuth2RestOperations restTemplate) {
		this.restTemplate = restTemplate; return this;
	}
	public OAuth2AuthenticationTokenServiceBuilder baseAuthorizationUrl(String url) {
		this.baseAuthorizationUrl = url; return this;
	}
	public OAuth2AuthenticationTokenServiceBuilder clientId(String clientId) {
		this.clientId = clientId; return this;
	}
	public OAuth2AuthenticationTokenServiceBuilder clientSecret(String secret) {
		this.clientSecret = secret; return this;
	}
	public OAuth2AuthenticationTokenServiceBuilder tokenName(String tokenName) {
		this.tokenName = tokenName; return this;
	}
	public OAuth2AuthenticationTokenServiceBuilder tokenConverter(
			OAuth2AuthenticationTokenConverter converter) {
		this.converter = converter; return this;
	}
	
	public abstract OAuth2AuthenticationTokenService build();
}
