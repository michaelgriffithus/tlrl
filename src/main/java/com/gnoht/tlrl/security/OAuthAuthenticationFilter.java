package com.gnoht.tlrl.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class OAuthAuthenticationFilter 
			extends AbstractAuthenticationProcessingFilter {

	protected static final Logger LOG =
			LoggerFactory.getLogger(OAuthAuthenticationFilter.class);
	
	private OAuthAuthenticationService oAuthAuthenticationService;
	
	public OAuthAuthenticationFilter() {
		super("/auth");
	}

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) 
					throws AuthenticationException, IOException, ServletException {
		LOG.debug("Starting attemptAuthentication()");
		
		OAuthAuthenticationToken authToken;
		String authCode = request.getParameter("code");

		if(authCode == null) {
			LOG.debug("No OAuth code detected, redirecting user to retrieve request Token.");
			String authorizationUrl = oAuthAuthenticationService.getAuthorizationUrl();
			LOG.debug("authenticationUrl: {}", authorizationUrl);
			response.sendRedirect(authorizationUrl);
			return null;
		} 
		
		LOG.debug("Found OAuth code, perform authentication get access Token");
		authToken = oAuthAuthenticationService.authenticate(authCode);
		
		return getAuthenticationManager().authenticate(authToken);
	}

	public void setoAuthAuthenticationService(
			OAuthAuthenticationService oAuthAuthenticationService) {
		this.oAuthAuthenticationService = oAuthAuthenticationService;
	}

}
