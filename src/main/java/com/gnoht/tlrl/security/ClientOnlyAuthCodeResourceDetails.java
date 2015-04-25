package com.gnoht.tlrl.security;

import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;

import com.gnoht.tlrl.config.SecurityConfig;

/**
 * Implementation of {@link AuthorizationCodeResourceDetails} for resources that
 * are available to client credentials grant. This is necessary since 
 * AuthorizationCodeResourceDetails returns false for the {@link #isClientOnly()}, 
 * and {@link AccessTokenProviderChain#obtainAccessToken} uses this check to 
 * prevent {@link SecurityConfig} anonymous() authentication. For example:
 * 
 * http()
 *  ..anonymous().disable() // is required with default AuthorizationCodeResourceDetails.
 * 
 */
public class ClientOnlyAuthCodeResourceDetails extends
		AuthorizationCodeResourceDetails {

	@Override
	public boolean isClientOnly() {
		return true;
	}
}
