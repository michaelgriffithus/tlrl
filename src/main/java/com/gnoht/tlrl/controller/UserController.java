package com.gnoht.tlrl.controller;

import javax.annotation.Resource;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.UserService;

/**
 * Controller for managing {@link User} specific requests.
 */
@Controller
@RequestMapping(value={"/api/users"})
public class UserController {
	
	@Resource private UserService userService;
	
	/**
	 * Returns 
	 * @param currentUser
	 * @return the currently authenticated user, otherwise throws security exception. 
	 */
	//@PreAuthorize("hasRole('USER')")
	@RequestMapping(value="/current", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User getCurrentUserName(@CurrentUser User currentUser) {
		return currentUser;
	}
}
