package com.gnoht.tlrl.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

public class OAuth2Authentication extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;
	protected static final Object NO_PRINCIPAL = null;
	
	private final Object principal;
	
	public OAuth2Authentication() {
		this(NO_PRINCIPAL);
	}
	
	/**
	 * Constructs an un-authenticated Authentication with given principal.
	 * 
	 * @param principal
	 */
	public OAuth2Authentication(Object principal) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.principal = principal; 
		super.setAuthenticated(false);
	}
	
	/**
	 * Constructs an Authentication from given authenticated principal and
	 * associated UserDetails.
	 * 
	 * @param principal
	 * @param userDetails
	 */
	public OAuth2Authentication(Object principal, UserDetails userDetails) {
		super(userDetails.getAuthorities());
		this.principal = principal;
		setDetails(userDetails);
		super.setAuthenticated(!getAuthorities().isEmpty());
	}

	@Override
	public void setAuthenticated(boolean authenticated) {
		if(authenticated)
			throw new IllegalArgumentException("Authentication can only be invalidated (set to false).");
		super.setAuthenticated(authenticated);
	}

	@Override
	public Object getCredentials() {
		return SecurityUtils.secureRandomStringKey();
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
