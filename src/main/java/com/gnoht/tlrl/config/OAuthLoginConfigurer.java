package com.gnoht.tlrl.config;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.gnoht.tlrl.security.OAuthAuthenticationFilter;
import com.gnoht.tlrl.security.OAuthAuthenticationProvider;
import com.gnoht.tlrl.security.OAuthAuthenticationService;
import com.gnoht.tlrl.security.OAuthAuthenticationToken;

/**
 * Custom configurer for OAuth security configuration.
 */
public final class OAuthLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
			AbstractAuthenticationFilterConfigurer<H, OAuthLoginConfigurer<H>, OAuthAuthenticationFilter> {

	private AuthenticationUserDetailsService<OAuthAuthenticationToken> authenticationUserDetailsService;
		
	protected OAuthLoginConfigurer() {
		//TODO: move to properties
		super(new OAuthAuthenticationFilter(), "/auth");
	}

	@Override
	protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
		return new AntPathRequestMatcher(loginProcessingUrl);
	}

	public OAuthLoginConfigurer<H> loginPage(String loginPage) {
		return super.loginPage(loginPage);
	}
	
	public OAuthLoginConfigurer<H> oAuthAuthenticationService(OAuthAuthenticationService service) {
		getAuthenticationFilter().setoAuthAuthenticationService(service);
		return this;
	}
	
	@Override
	public void init(H http) throws Exception {
		super.init(http);
		
		http.addFilterBefore(getAuthenticationFilter(), SwitchUserFilter.class);
		
		OAuthAuthenticationProvider provider = new OAuthAuthenticationProvider();
		provider.setUserDetailsService(getAuthenticationUserDetailsService(http));
		provider = postProcess(provider);
		http.authenticationProvider(provider);
		
	}
	
	@Override
	public void configure(H http) throws Exception {
		super.configure(http);
	}

	public OAuthLoginConfigurer<H> authenticationUserDetailsService(
			AuthenticationUserDetailsService<OAuthAuthenticationToken> service) {
		this.authenticationUserDetailsService = service;
		return this;
	}
	
	private AuthenticationUserDetailsService<OAuthAuthenticationToken> 
			getAuthenticationUserDetailsService(H http) {
		if(authenticationUserDetailsService != null) {
			return authenticationUserDetailsService;
		}
		return new UserDetailsByNameServiceWrapper<OAuthAuthenticationToken>(http.getSharedObject(UserDetailsService.class));
	}
}
