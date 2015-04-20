package com.gnoht.tlrl.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.gnoht.tlrl.security.GoogleOAuth2AuthenticationTokenService;
import com.gnoht.tlrl.security.OAuth2AuthenticationProcessingFilter;
import com.gnoht.tlrl.security.OAuth2AuthenticationProvider;
import com.gnoht.tlrl.security.OAuth2AuthenticationTokenService;
import com.gnoht.tlrl.security.OAuth2AuthenticationUserDetailsService;
import com.gnoht.tlrl.security.SecurityPackage;

@Configuration
@ComponentScan(basePackageClasses={SecurityPackage.class})
@EnableWebSecurity // @EnableWebMvcSecurity is deprecated as 4.0
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	public static final String SIGNIN_URL = "/";
	public static final String SIGNUP_URL = "/signup";
	public static final String SIGNOUT_URL = "/signout";
	public static final String AUTH_CATCHALL_URL = "/auth/*"; 
	public static final String[] SECURED_URLS = {"/api/urls"};
	
	@Resource private Environment env;
	@Resource private OAuth2ClientContextFilter oAuth2ClientContextFilter;
	@Resource private OAuth2AuthenticationUserDetailsService userDetailsService;
	@Resource private OAuth2RestOperations restTemplate;

	/**
	 * @return a Google specific {@link OAuth2AuthenticationTokenService} to handle
	 * OAuth communication with Google OAuth services. 
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
			.authenticationEntryPoint(delegatingAuthenticationEntryPoint())
		.and()
			.exceptionHandling()
				.accessDeniedPage(SIGNIN_URL)
		.and()
			.anonymous().disable()
			.authorizeRequests()
				.antMatchers(
						SIGNOUT_URL,
						AUTH_CATCHALL_URL)
					.permitAll()
				.antMatchers("/api/urls")
					.hasRole("USER")
		.and()
			.logout()
				.deleteCookies("JSESSIONID")
				// Note: unless CSRF is disable, we must "signout" via POST vs GET
				// http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#csrf-logout
				.logoutUrl(SIGNOUT_URL) 
				.logoutSuccessUrl(SIGNIN_URL)
		.and()
			.addFilterAfter(oAuth2ClientContextFilter, ExceptionTranslationFilter.class)
			.addFilterBefore(googleOAuthProcessingFilter(), FilterSecurityInterceptor.class);
		
		
	}
	
	/**
	 * @return {@link AuthenticationEntryPoint} that handles both RESTful clients
	 * and normal webapp request.
	 */
	protected DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
		entryPoints.put(new SecuredRequestMatcher(), new Http403ForbiddenEntryPoint());

		DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		delegatingEntryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint(SIGNIN_URL));

		return delegatingEntryPoint;
	}
	
	/**
	 * Configure security to use our custom {@link AuthenticationProvider}.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(new OAuth2AuthenticationProvider(userDetailsService));
	}

	/**
	 * @return Security filter to handle Google OAuth logins.
	 * @throws Exception
	 */
	@Bean
	protected Filter googleOAuthProcessingFilter() throws Exception {
		OAuth2AuthenticationProcessingFilter filter = 
				new OAuth2AuthenticationProcessingFilter("/auth/google");
		filter.setOAuthAuthenticationTokenService(googleAuthenticationTokenService());
		filter.setAuthenticationManager(authenticationManager());
		return filter;
	}
	
	/**
	 * {@link RequestMatcher} that checks requested url to a list of secured urls.
	 */
	final class SecuredRequestMatcher implements RequestMatcher {
		final Map<String, String[]> securedMethodToUrlMap;
		
		public SecuredRequestMatcher() {
			securedMethodToUrlMap = new HashMap<String, String[]>();
			securedMethodToUrlMap.put(GET.name(), SECURED_URLS);
			securedMethodToUrlMap.put(PUT.name(), SECURED_URLS);
			securedMethodToUrlMap.put(POST.name(), SECURED_URLS);
			securedMethodToUrlMap.put(DELETE.name(), SECURED_URLS);
		}
		
		@Override
		public boolean matches(HttpServletRequest request) {
			String url = UrlUtils.buildRequestUrl(request);
			String method = request.getMethod();
			String[] securedUrls = securedMethodToUrlMap.get(method);
			if(securedUrls != null) {
				for(String securedUrl : securedUrls) {
					if(url.startsWith(securedUrl)) 
						return true;
				}
			}
			return false;
		}
	}
}
