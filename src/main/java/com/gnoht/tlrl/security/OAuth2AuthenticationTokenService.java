package com.gnoht.tlrl.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

/**
 * Service gets OAuth2 access token and converts it to a local authentication.
 */
public interface OAuth2AuthenticationTokenService {

	OAuth2Authentication getAuthentication() throws AuthenticationException;
	OAuth2Authentication getAuthentication(OAuth2AccessToken accessToken) throws AuthenticationException;
	OAuth2AccessToken getAccessToken() throws AuthenticationException; 
	
}
