package com.gnoht.tlrl.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static com.gnoht.tlrl.security.SecurityUtils.*;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.gnoht.tlrl.config.OAuth2SecurityConfig.GoogleOauth2;
import com.gnoht.tlrl.security.GoogleOAuth2AuthenticationTokenService;
import com.gnoht.tlrl.security.OAuth2AuthenticationProcessingFilter;
import com.gnoht.tlrl.security.OAuth2AuthenticationProvider;
import com.gnoht.tlrl.security.OAuth2AuthenticationSuccessHandler;
import com.gnoht.tlrl.security.OAuth2AuthenticationTokenService;
import com.gnoht.tlrl.security.OAuth2AuthenticationUserDetailsService;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.security.SecurityPackage;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.RememberMeTokenService;

@Configuration
@ComponentScan(basePackageClasses={SecurityPackage.class})
@EnableWebSecurity // @EnableWebMvcSecurity is deprecated as 4.0
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

	public static final String SIGNIN_URL = "/";
	public static final String SIGNUP_URL = "/signup";
	public static final String SIGNOUT_URL = "/signout";
	public static final String AUTH_CATCHALL_URL = "/auth/*"; 
	public static final String[] SECURED_GET_URLS = {"/bm/add", "/bm/add/**", "/api/users/current"};
	public static final String[] SECURED_DELETE_URLS = {"/api/urls/**"};
	public static final String[] SECURED_POST_URLS = {"/api/urls", "/api/urls/**"};
	public static final String[] SECURED_PUT_URLS = {"/api/urls/**"};
	
	@Resource private Environment env;
	@Resource private OAuth2ClientContextFilter oAuth2ClientContextFilter;
	@Resource private OAuth2AuthenticationUserDetailsService userDetailsService;
	@Autowired @GoogleOauth2
	private OAuth2AuthenticationTokenService oAuth2AuthtokenService; 
	
	@Resource(name="rememberMeTokenService")
	private RememberMeTokenService rememberMeTokenService;
	
	@Value("app.security.rememberMe.cookieName")
	private String rememberMeCookieName;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers(
					"/favicon.ico",
					"/static/**",
					"/scripts/**",
					"/views/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.httpBasic()
			.authenticationEntryPoint(delegatingAuthenticationEntryPoint())
		.and()
			.exceptionHandling()
				.accessDeniedPage(SIGNUP_URL)
		.and()
			//.anonymous().disable() 
			.authorizeRequests()
				//URLs for all users
				.antMatchers(
							"/recent",
							"/@**",
							"/popular",
							"/help",
							"/about",
							"/error/**",
							AUTH_CATCHALL_URL,
							SIGNOUT_URL,
							SIGNIN_URL)
					.access("!hasRole('"+ UNCONFIRMED_ROLE_ID +"')")
				//URLS for users with 'unconfirmed' role	
				.antMatchers( 
							SIGNUP_URL)
					.hasRole(UNCONFIRMED_ROLE_ID)
				//URLs for users with 'user' role		
				.antMatchers(GET, SECURED_GET_URLS).hasRole(USER_ROLE_ID)
				.antMatchers(PUT, SECURED_PUT_URLS).hasRole(USER_ROLE_ID)
				.antMatchers(POST, SECURED_POST_URLS).hasRole(USER_ROLE_ID)
				.antMatchers(DELETE, SECURED_DELETE_URLS).hasRole(USER_ROLE_ID)
		.and()
			.csrf().disable() // TODO: remove after test
			.logout()
				.deleteCookies("JSESSIONID", rememberMeCookieName)
				// Note: unless CSRF is disable, we must "signout" via POST vs GET
				// http://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#csrf-logout
				.logoutUrl(SIGNOUT_URL) 
				.logoutSuccessUrl(SIGNIN_URL)
		.and()
			.addFilterAfter(oAuth2ClientContextFilter, ExceptionTranslationFilter.class)
			.addFilterBefore(googleOAuthProcessingFilter(), FilterSecurityInterceptor.class)
		.rememberMe()
			.rememberMeServices(rememberMeServices());
		
		
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
	
	@Bean
	public RememberMeServices rememberMeServices() {
		PersistentTokenBasedRememberMeServices service = new PersistentTokenBasedRememberMeServices(
				SecurityUtils.secureRandomStringKey(), userDetailsService, rememberMeTokenService);
		service.setCookieName(rememberMeCookieName);
		service.setAlwaysRemember(Boolean.valueOf(env.getRequiredProperty("app.security.rememberMe.alwaysRemember")));
		service.setUseSecureCookie(Boolean.valueOf(env.getRequiredProperty("app.security.rememberMe.useSecureCookie")));
		return service;
	}
	
	@Bean
	public AuthenticationSuccessHandler authenticationSuccessHandler() {
		return new OAuth2AuthenticationSuccessHandler();
	}

	/**
	 * @return Security filter to handle Google OAuth logins.
	 * @throws Exception
	 */
	@Bean
	@GoogleOauth2
	protected Filter googleOAuthProcessingFilter() throws Exception {
		OAuth2AuthenticationProcessingFilter filter = 
				new OAuth2AuthenticationProcessingFilter("/auth/google");
		filter.setRememberMeServices(rememberMeServices());
		filter.setOAuthAuthenticationTokenService(oAuth2AuthtokenService);
		filter.setAuthenticationManager(authenticationManager());
		filter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
		return filter;
	}
	
	/**
	 * {@link RequestMatcher} that checks requested url to a list of secured urls.
	 */
	final class SecuredRequestMatcher implements RequestMatcher {
		final Map<String, String[]> securedMethodToUrlMap;
		
		public SecuredRequestMatcher() {
			securedMethodToUrlMap = new HashMap<String, String[]>();
			securedMethodToUrlMap.put(GET.name(), SECURED_GET_URLS);
			securedMethodToUrlMap.put(PUT.name(), SECURED_PUT_URLS);
			securedMethodToUrlMap.put(POST.name(), SECURED_POST_URLS);
			securedMethodToUrlMap.put(DELETE.name(), SECURED_DELETE_URLS);
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
