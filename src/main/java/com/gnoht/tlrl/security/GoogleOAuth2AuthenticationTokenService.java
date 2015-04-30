package com.gnoht.tlrl.security;

import java.util.Map;

import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * Google OAuth2 specific implementation of {@link OAuth2AuthenticationTokenService}. 
 * Specifically it passes "access_token" param to the authorization end point. 
 */
public class GoogleOAuth2AuthenticationTokenService 
		extends AbstractOAuth2AuthenticationTokenService {

	private GoogleOAuth2AuthenticationTokenService(
			OAuth2RestOperations restTemplate, String url, String clientId,
			String clientSecret, String tokenName) {
		
		super(restTemplate, url, clientId, clientSecret, tokenName, 
				new OAuth2AuthenticationTokenConverter() {
			@Override
			public OAuth2Authentication extractAuthentication(
					Map<String, Object> tokenInfo) {
				return new OAuth2Authentication(tokenInfo.get("email"));
			}
		});
	}

	@Override
	protected String getAuthorizationUrl(OAuth2AccessToken accessToken) {
		return getBaseAuthorizationUrl() + "?access_token=" + accessToken.getValue();
	}

	public static Builder getBuilder(OAuth2RestOperations restTemplate) {
		return new Builder(restTemplate);
	}
	
	/**
	 * Helper for building {@link GoogleOAuth2AuthenticationTokenService}.  
	 */
	public static class Builder extends OAuth2AuthenticationTokenServiceBuilder {
		
		public Builder(OAuth2RestOperations restTemplate) {
			super(restTemplate);
		}
		
		@Override
		public OAuth2AuthenticationTokenService build() {
			return new GoogleOAuth2AuthenticationTokenService(
					restTemplate, baseAuthorizationUrl, clientId, 
					clientSecret, tokenName);
		}
	}
}
