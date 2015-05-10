package com.gnoht.tlrl.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

/**
 * 
 */
public class OAuth2AuthenticationProcessingFilter extends
		AbstractAuthenticationProcessingFilter {

	private OAuth2AuthenticationTokenService oauthAuthenticationTokenService;
	
	public OAuth2AuthenticationProcessingFilter(
			String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException, IOException,
			ServletException {
		try {
			OAuth2AccessToken accessToken = oauthAuthenticationTokenService.getAccessToken();
			Authentication authentication = oauthAuthenticationTokenService.getAuthentication(accessToken);
			return getAuthenticationManager().authenticate(authentication);
		} catch(OAuthException e) {
			throw new AccessDeniedException("Unable to process OAuth authentication:", e);
		} 
	}

	public void setOAuthAuthenticationTokenService(
			OAuth2AuthenticationTokenService authenticationTokenService) {
		this.oauthAuthenticationTokenService = authenticationTokenService;
	}

}
