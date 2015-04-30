package com.gnoht.tlrl.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Simple {@link AuthenticationProvider} responsible for loading an 
 * {@link Authentication}'s {@link UserDetails}, which would provide the 
 * authorities needed to make the Authentication complete. 
 */
public class OAuth2AuthenticationProvider 
		implements AuthenticationProvider {

	protected static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);
	
	private AuthenticationUserDetailsService<OAuth2Authentication> userDetailsService;

	public OAuth2AuthenticationProvider(AuthenticationUserDetailsService<OAuth2Authentication> userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	/*
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(
	 * 	org.springframework.security.core.Authentication)
	 */
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		LOG.debug("Starting authenticate(): authentication={}", authentication);
		
		if(authentication == null || !supports(authentication.getClass())) 
			throw new IllegalArgumentException("Unsupported Authentication class!");
		 
		if(authentication.isAuthenticated()) { 
			LOG.debug("Already authenticated, returning current authentication!");
			return authentication;
		} else {
			LOG.debug("Not fulled authenticated, try to find and load associated UserDetails.");
			OAuth2Authentication oauthAuth = (OAuth2Authentication) authentication;
			UserDetails userDetails = userDetailsService.loadUserDetails(oauthAuth);
			return createSuccessfulAuthentication(oauthAuth, userDetails);
		}
	}

	private Authentication createSuccessfulAuthentication(
			OAuth2Authentication oauthAuth, UserDetails userDetails) {
		return new OAuth2Authentication(userDetails, userDetails);
	}

	/*
	 * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
	 */
	@Override
	public boolean supports(Class<?> authClass) {
		return OAuth2Authentication.class.isAssignableFrom(authClass);
	}
}
