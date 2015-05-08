package com.gnoht.tlrl.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;

/**
 * Controller for managing request to the our SPA (single page application).
 */
@Controller
public class SpaController {

	private static final Logger LOG = LoggerFactory.getLogger(SpaController.class);
	
	/**
	 * Handles request to our SPA, specifically to the resource that bootstraps
	 * the rest of our single page application.
	 *   
	 * @param user currently authenticated User or null
	 * @param model use to set the currently authenticated User 
	 * @return view name of SPA resource
	 */
	@RequestMapping(value={"/@*", "/@*/*", "/search", "/urls/**", "/popular", "/recent"})
	public String app(@CurrentUser User user, Model model) {
		if(user != null) { 
			LOG.debug("Adding model attribute user={}", user);
			model.addAttribute("user", user);
		}
		return "app";
	}
}
