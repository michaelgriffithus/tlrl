package com.gnoht.tlrl.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.gnoht.tlrl.config.SecurityConfig;

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
		OAuth2AccessToken accessToken = oauthAuthenticationTokenService.getAccessToken();
		Authentication authentication = oauthAuthenticationTokenService.getAuthentication(accessToken);
		return getAuthenticationManager().authenticate(authentication);
	}

	public void setOAuthAuthenticationTokenService(
			OAuth2AuthenticationTokenService authenticationTokenService) {
		this.oauthAuthenticationTokenService = authenticationTokenService;
	}

}
