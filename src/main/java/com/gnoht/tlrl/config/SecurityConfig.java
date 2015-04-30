package com.gnoht.tlrl.config;

import java.io.IOException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.security.OAuthAuthenticationFilter;
import com.gnoht.tlrl.security.OAuthAuthenticationProvider;
import com.gnoht.tlrl.security.OAuthAuthenticationService;
import com.gnoht.tlrl.security.OAuthAuthenticationToken;
import com.gnoht.tlrl.security.OAuthUserDetails;
import com.gnoht.tlrl.security.SecurityUtils;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Resource private Environment env;
	
	@Resource(name="userDetailsService")
	private AuthenticationUserDetailsService<OAuthAuthenticationToken> userDetailsService;
	
	@Resource(name="oAuthAuthenticationService")
	private OAuthAuthenticationService oAuthAuthenticationService;
	
	@Resource(name="rememberMeServices")
	private RememberMeServices rememberMeServices;
	
	@Resource private SecurityUtils securityUtils;
	
	@Bean @Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	public static final String ROLE_USER = "USER";
	public static final String ROLE_UNCONFIRMED = "UNCONFIRMED";
	
	public static final String SIGNUP_PATH = "/signup";
	public static final String SIGNIN_PATH = "/";
	public static final String SIGNOUT_PATH = "/signout";
	public static final String ERROR_401_PATH = "/errors/401";
	public static final String[] RESTRICTED_GET_PATHS = {"/bm/add", "/bm/add/**", "/api/users/current"};
	public static final String[] RESTRICTED_POST_PATHS = {"/api/urls", "/api/urls/**"};
	public static final String[] RESTRICTED_PUT_PATHS = {"/api/urls", "/api/urls/**"};
	public static final String[] RESTRICTED_DELETE_PATHS = {"/api/urls", "/api/urls/**"};
	
	@Bean
	public OAuthAuthenticationFilter oAuthAuthenticationFilter(OAuthAuthenticationService service) throws Exception {
		OAuthAuthenticationFilter filter = new OAuthAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManager());
		filter.setoAuthAuthenticationService(oAuthAuthenticationService);
		return filter;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider())
			.authenticationProvider(rememberMeAuthenticationProvider());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers(
					"/release.txt",
					"/static/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.headers().cacheControl().disable()
			.and()
				.httpBasic()
					// NO authentication exists yet
					/* adds Http403ForbiddenEntryPoint which sends back 403 HTTP 
					 * Status for unauthenticated /api calls*/
					.authenticationEntryPoint(delegatingAuthenticationEntryPoint())
			.and()
				// we have authentication, but not authorized
				.exceptionHandling()
				//.accessDeniedHandler(new UnconfirmedUserDeniedHandler())
				.accessDeniedPage(SIGNIN_PATH) //if user is authenticated, but lack authorization
			.and()
				//mappings that required authentication
				.authorizeRequests()
					.antMatchers(
							"/auth", 				//trigger the auth process, auth maps to a filter 
							SIGNOUT_PATH, 		//path triggers a signout event (e.g. delete cookies, session..)
							"/error/**",		//error pages
							"/errors/**")		//
						.permitAll()
					//handles the signup process
					.antMatchers(
							SIGNUP_PATH,		//show the signup form. come here after success auth from OP, but user not in our system
							"/api/signup") 	//do actual signup, called from signup/form
						.hasRole(ROLE_UNCONFIRMED)
					.antMatchers(GET, RESTRICTED_GET_PATHS).hasRole(ROLE_USER)
					.antMatchers(POST, RESTRICTED_POST_PATHS).hasRole(ROLE_USER)
					.antMatchers(PUT, RESTRICTED_PUT_PATHS).hasRole(ROLE_USER)
					.antMatchers(DELETE, RESTRICTED_DELETE_PATHS).hasRole(ROLE_USER)
					.anyRequest()
						.not().hasRole(ROLE_UNCONFIRMED)
			.and()
				.apply(new OAuthLoginConfigurer<HttpSecurity>())
					.authenticationUserDetailsService(userDetailsService)
					.oAuthAuthenticationService(oAuthAuthenticationService)
					.loginProcessingUrl("/auth")
					.successHandler(new OAuthAuthenticationSuccessHandler())
			.and()
				.csrf().disable()
				.logout()
					.deleteCookies("JSESSIONID","SPRING_SECURITY_REMEMBER_ME_COOKIE")
					.logoutUrl(SIGNOUT_PATH)
					.logoutSuccessUrl(SIGNIN_PATH)
			.and()
				.rememberMe()
					.rememberMeServices(rememberMeServices);
	
	}

	class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
		@Override
		public void onAuthenticationSuccess(HttpServletRequest request,
				HttpServletResponse response, Authentication auth) throws IOException,
				ServletException {
			if(auth.getDetails() instanceof OAuthUserDetails &&
					securityUtils.isUnconfirmedUser((OAuthUserDetails) auth.getDetails())) {
					response.sendRedirect(SIGNUP_PATH);
			} else {
				DefaultSavedRequest savedRequest = (DefaultSavedRequest) request
						.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
				response.sendRedirect(savedRequest == null ? "/@" + auth.getName() + "/urls" : savedRequest.getRedirectUrl());
			}
		}
	}
	
	class UnconfirmedUserDeniedHandler implements AccessDeniedHandler {
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException e) throws IOException, ServletException {
			Principal principal = request.getUserPrincipal();
			if(principal instanceof OAuthAuthenticationToken && securityUtils.isUnconfirmedUser
					((OAuthUserDetails)((OAuthAuthenticationToken) principal).getDetails())) {
				response.sendRedirect(SIGNUP_PATH);
			} else {
				response.sendRedirect(ERROR_401_PATH);
			}
		}
	}
	
	/*
	 * Delegated to here where there is no authentication yet.
	 */
	protected DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();
		entryPoints.put(new ForbiddenApiRequestMatcher(), new Http403ForbiddenEntryPoint());

		DelegatingAuthenticationEntryPoint delegatingEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
		delegatingEntryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint(SIGNIN_PATH));

		return delegatingEntryPoint;
	}
	
	class ForbiddenApiRequestMatcher implements RequestMatcher {
		final Map<String, String[]> forbiddenMethodToUrlMap;
		
		public ForbiddenApiRequestMatcher() {
			forbiddenMethodToUrlMap = new HashMap<String, String[]>();
			forbiddenMethodToUrlMap.put(GET.name(), new String[]{"/api/users/current"});
			forbiddenMethodToUrlMap.put(PUT.name(), RESTRICTED_PUT_PATHS);
			forbiddenMethodToUrlMap.put(POST.name(), RESTRICTED_POST_PATHS);
			forbiddenMethodToUrlMap.put(DELETE.name(), RESTRICTED_DELETE_PATHS);
		}
		
		@Override
		public boolean matches(HttpServletRequest request) {
			String url = UrlUtils.buildRequestUrl(request);
			String method = request.getMethod();
			for(String m: forbiddenMethodToUrlMap.keySet()) {
				if(m.equals(method)) {
					for(String restrictedUrl: forbiddenMethodToUrlMap.get(m)) {
						if(url.startsWith(restrictedUrl)) return true;
					}
				}
			}
			
			return false;
		}
	}
	
	protected RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
		return new RememberMeAuthenticationProvider(env.getRequiredProperty("app.security.rememberMe.cookieKey"));
	}
	
	protected AuthenticationProvider authenticationProvider() {
		return new OAuthAuthenticationProvider(userDetailsService, new SimpleAuthorityMapper());
	}
	
}
