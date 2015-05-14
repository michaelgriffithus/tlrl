package com.gnoht.tlrl.controller;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.gnoht.tlrl.config.SecurityConfig;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.security.SecurityUtils;

/**
 * Controller for managing request to the non API/SPA portions of the 
 * application that require special handling (i.e, can't simply be served 
 * up with Spring built-in ViewController).
 */
@Controller
public class WebAppController {

	private static final Logger LOG = LoggerFactory.getLogger(WebAppController.class);

	@Autowired
	AuthenticationManager authenticationManager;
	
	/**
	 * Mapping for serving the "home" page. Depending on currently authenticated
	 * status, home can be either generic home page or user's home page.
	 * 
	 * @param user
	 * @return view name to generic home page or redirect to user's home.
	 */
	@RequestMapping(value={"/"}, method=RequestMethod.GET)
	public String home(@CurrentUser User user) {
		LOG.info("Starting home(): user={}", user);
		if(user != null) {
			if(SecurityUtils.hasRole(user, SecurityUtils.ROLE_USER))
				return "redirect:/@" + user.getName();
			if(SecurityUtils.hasRole(user, SecurityUtils.ROLE_UNCONFIRMED))
				return "redirect:" + SecurityConfig.SIGNUP_URL;
		}
		return "index";
	}
}
