package com.gnoht.tlrl.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.gnoht.tlrl.security.OAuthAuthenticationStatus;
import com.gnoht.tlrl.security.OAuthUserDetails;

public class OAuthAuthenticationToken 
		extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;

	protected static final Object NO_PRINCIPAL = null;
	
	private final OAuthAuthenticationStatus status;
	private final Object principal;

	/**
	 * Constructs {@link Authentication} based on given status. Use when
	 * we have a failed/error authentication attempt.
	 *   
	 * @param status
	 */
	public OAuthAuthenticationToken(OAuthAuthenticationStatus status) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.status = status;
		this.principal = NO_PRINCIPAL;
	}
	
	/**
	 * Constructs {@link Authentication} based on given status and principal.
	 * Not yet authenticated, since we're not getting any {@link GrantedAuthority}s.
	 * 
	 * @param status
	 * @param principal
	 */
	public OAuthAuthenticationToken(OAuthAuthenticationStatus status, Object principal) {
		super(AuthorityUtils.NO_AUTHORITIES);
		this.status = status;
		this.principal = principal;
		setAuthenticated(false);
	}
	
	/**
	 * Constructs {@link Authentication} based on given status, principal, 
	 * and userDetails. If {@link GrantedAuthority}s exists in given 
	 * {@link UserDetails}, we can assumed we've been fully authenticated.
	 * 
	 * @param status
	 * @param principal
	 * @param userDetails
	 */
	public OAuthAuthenticationToken(OAuthAuthenticationStatus status, 
			Object principal, UserDetails userDetails) {
		super(userDetails.getAuthorities());
		this.status = status;
		this.principal = principal;
		setDetails(userDetails);
		setAuthenticated(!getAuthorities().isEmpty());
	}

	@Override
	public Object getCredentials() {
		return null;
	}
	
	public String getEmail() {
		if(principal instanceof OAuthUserDetails) {
			return ((OAuthUserDetails) principal).getEmail();
		} 
		return principal == null ? "" : principal.toString();
	}
	
	public OAuthAuthenticationStatus status() {
		return status;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

	@Override
	public UserDetails getDetails() {
		return (UserDetails) super.getDetails();
	}

}
