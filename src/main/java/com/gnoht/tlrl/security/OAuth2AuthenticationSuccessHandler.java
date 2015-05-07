package com.gnoht.tlrl.security;

import static com.gnoht.tlrl.security.SecurityUtils.ROLE_UNCONFIRMED;
import static com.gnoht.tlrl.security.SecurityUtils.hasRole;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import com.gnoht.tlrl.config.SecurityConfig;

public class OAuth2AuthenticationSuccessHandler implements
		AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication auth) throws IOException,
			ServletException {
		if(hasRole(auth.getPrincipal(), ROLE_UNCONFIRMED)) {
			response.sendRedirect(SecurityConfig.SIGNUP_URL);
		} else {
			DefaultSavedRequest savedRequest = (DefaultSavedRequest) 
					request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
			response.sendRedirect(savedRequest == null ? 
					"/@" + auth.getName() : savedRequest.getRedirectUrl());
		}
	}
}

