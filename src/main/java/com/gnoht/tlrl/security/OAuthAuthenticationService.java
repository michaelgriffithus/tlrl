package com.gnoht.tlrl.security;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public interface OAuthAuthenticationService extends OAuthService {

	/**
	 * Authenticates using a returned verifier token from OAuth server.
	 * @param verifier
	 * @return
	 */
	public OAuthAuthenticationToken authenticate(Token verifier);
	
	/**
	 * Authenticates using a returned verifier code from OAuth server.
	 * @param verifier
	 * @return
	 */
	public OAuthAuthenticationToken authenticate(String verifier);
	public String getAuthorizationUrl();
}
