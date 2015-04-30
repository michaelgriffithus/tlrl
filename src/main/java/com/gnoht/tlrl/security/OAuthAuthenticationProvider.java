package com.gnoht.tlrl.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import com.gnoht.tlrl.security.OAuthAuthenticationStatus.Code;

public class OAuthAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOG =
			LoggerFactory.getLogger(OAuthAuthenticationProvider.class);
	
	private AuthenticationUserDetailsService<OAuthAuthenticationToken> userDetailsService;
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
	
	public OAuthAuthenticationProvider() {}
	
	public OAuthAuthenticationProvider(
			AuthenticationUserDetailsService<OAuthAuthenticationToken> userDetailsService, 
			GrantedAuthoritiesMapper authoritiesMapper) {
		this.userDetailsService = userDetailsService;
		this.authoritiesMapper = authoritiesMapper;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		//make sure we're dealing with an OAuthAuthenticationToken
		if(!supports(authentication.getClass())) {
			return null;
		}
		
		//no use to continue if user is already authenticated
		if(authentication.isAuthenticated()) {
			return authentication;
		}
		
		//perform authentication based on given AuthenticationToken
		if(authentication instanceof OAuthAuthenticationToken) {
			LOG.info("Is supported AuthenticationToken, but not authenticated yet!");
			OAuthAuthenticationToken oAuthToken = (OAuthAuthenticationToken) authentication;
			if(oAuthToken.status().is(Code.SUCCESS)) {
				/* At this point, we have a valid response from OAuth provider, we just
				 * need to see if user is in our system by pulling up their details.
				 * If user doesn't exists, UserDetailsService will create a user. */
				UserDetails userDetails = userDetailsService.loadUserDetails(oAuthToken);
				return createSuccessfulAuthentication(oAuthToken, userDetails);
			} else if(oAuthToken.status().is(Code.ERROR)) {
				throw new AuthenticationServiceException(
					"Error message from server: " + oAuthToken.status().getMessage());
			} else {
				throw new AuthenticationServiceException(
					"Unrecognized OAuth Provider return status " + oAuthToken.status());
			}
		}
		LOG.debug("authentication was unsuccessful!");
		return null;
	}
	
	protected Authentication createSuccessfulAuthentication(
			OAuthAuthenticationToken authToken, UserDetails userDetails) {
		return new OAuthAuthenticationToken(authToken.status(), userDetails, userDetails); 
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuthAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setUserDetailsService(
			AuthenticationUserDetailsService<OAuthAuthenticationToken> userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
	}
}
