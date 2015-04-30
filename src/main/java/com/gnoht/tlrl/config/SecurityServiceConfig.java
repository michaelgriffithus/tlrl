package com.gnoht.tlrl.config;

import javax.annotation.Resource;

import org.scribe.builder.ServiceBuilder;
import org.scribe.oauth.OAuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.gnoht.tlrl.security.OAuthAuthenticationToken;
import com.gnoht.tlrl.security.GoogleApi2;

@Configuration
public class SecurityServiceConfig {

	@Resource private Environment env;

	@Resource(name="userDetailsService")
	private AuthenticationUserDetailsService<OAuthAuthenticationToken> userDetailsService;
	
	@Resource(name="rememberMeTokenService")
	private PersistentTokenRepository persistentTokenRepository;
	
	@Bean(name="rememberMeServices")
	public RememberMeServices rememberMeServices() {
		PersistentTokenBasedRememberMeServices service = new PersistentTokenBasedRememberMeServices(
				env.getRequiredProperty("app.security.rememberMe.cookieKey"), 
				(UserDetailsService) userDetailsService, persistentTokenRepository);
		
		service.setAlwaysRemember(Boolean.valueOf(env.getRequiredProperty("app.security.rememberMe.alwaysRemember")));
		service.setUseSecureCookie(Boolean.valueOf(env.getRequiredProperty("app.security.rememberMe.useSecureCookie")));
		return service;
	}
	
	@Bean(name="oAuthAuthenticationService")
	public OAuthService googleOAuthService() {
		return new ServiceBuilder()
			.apiKey(env.getRequiredProperty("googleOAuthApiKey"))
			.apiSecret(env.getRequiredProperty("googleOAuthApiSecret"))
			.callback(env.getRequiredProperty("googleOAuthCallback"))
			.scope("email")
			.provider(GoogleApi2.class)
			.build();
	}

}
