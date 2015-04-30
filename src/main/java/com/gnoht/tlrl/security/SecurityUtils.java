package com.gnoht.tlrl.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;

@Component("securityUtils")
public final class SecurityUtils {

	private static final String UNCONFIRMED_ROLE_ID = "ROLE_UNCONFIRMED";
	
	private SecureRandom secureRandom;

	@Resource private RememberMeServices rememberMeServices;
	@Resource private UserDetailsService userDetailsService;
	
	public SecurityUtils() {
		try {
			secureRandom = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Failed find SHA1PRNG algorithm!");
		}
	}
	
	public String createRandom() {
		return Long.toString(secureRandom.nextLong());
	}

	public boolean isUnconfirmedUser(User user) {
		return (user != null && user.getRole() != null &&
				UNCONFIRMED_ROLE_ID.equals(user.getRole().getId()));
	}
	
	public void reloadUserDetails(User user, 
				HttpServletRequest request, HttpServletResponse response) {
		
		OAuthUserDetails userDetails = (OAuthUserDetails) 
					userDetailsService.loadUserByUsername(user.getName());
		OAuthAuthenticationToken authToken = 
			new OAuthAuthenticationToken(new OAuthAuthenticationStatus(),
					userDetails, userDetails);
		
		SecurityContextHolder.getContext().setAuthentication(authToken);
		rememberMeServices.loginSuccess(request, response, authToken);
	}
	
}
