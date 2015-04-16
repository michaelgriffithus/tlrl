package com.gnoht.tlrl.config;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.gnoht.tlrl.security.GoogleOAuth2AuthenticationTokenService;
import com.gnoht.tlrl.security.OAuth2AuthenticationProcessingFilter;
import com.gnoht.tlrl.security.OAuth2AuthenticationTokenService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Resource private Environment env;
	@Resource private OAuth2ClientContextFilter oAuth2ClientContextFilter;
	@Resource private OAuth2RestOperations restTemplate;

	/**
	 * @return a Google specific {@link OAuth2AuthenticationTokenService}. 
	 */
	@Bean public OAuth2AuthenticationTokenService googleAuthenticationTokenService() {
		return GoogleOAuth2AuthenticationTokenService
			.getBuilder(restTemplate)
				.clientId(env.getRequiredProperty("oauth2.google.client.id"))
				.clientSecret(env.getRequiredProperty("oauth2.google.client.secret"))
				.baseAuthorizationUrl(env.getRequiredProperty("oauth2.google.baseAuthorizationUrl"))
				.tokenName(env.getRequiredProperty("oauth2.google.tokenName"))
			.build();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.httpBasic()
			.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/googleLogin"))
		.and()
			.anonymous().disable()
			.authorizeRequests()
				.antMatchers("/**").fullyAuthenticated()
		.and()
			.addFilterAfter(oAuth2ClientContextFilter, ExceptionTranslationFilter.class)
			.addFilterBefore(googleOAuthProcessingFilter(), FilterSecurityInterceptor.class)
		.authenticationProvider(new AuthenticationProvider() {
			@Override
			public boolean supports(Class<?> arg0) {
				System.out.println("--------------------");
				return false;
			}
			@Override
			public Authentication authenticate(Authentication arg0)
					throws AuthenticationException {
				return null;
			}
		});		
	}

	@Bean
	protected Filter googleOAuthProcessingFilter() {
		OAuth2AuthenticationProcessingFilter filter = 
				new OAuth2AuthenticationProcessingFilter("/googleLogin");
		filter.setOAuthAuthenticationTokenService(googleAuthenticationTokenService());
		filter.setAuthenticationManager(new AuthenticationManager() {
			@Override
			public Authentication authenticate(Authentication arg0)
					throws AuthenticationException {
				System.out.println("----------- no op:" + arg0);
				return null;
			}
		});
		return filter;
	}
}
