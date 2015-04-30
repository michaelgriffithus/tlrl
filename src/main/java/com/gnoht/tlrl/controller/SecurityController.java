package com.gnoht.tlrl.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static com.gnoht.tlrl.security.SecurityUtils.*;

import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.persistence.NonUniqueResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.UserService;

//@Controller
public class SecurityController {
	
	@Resource private UserService userService;
	
	@RequestMapping(value={"/unauthorized", "/signup"}, method={GET})
	public String unauthorized(@CurrentUser User currentUser, Model model) {
		if(hasRole(currentUser, ROLE_UNCONFIRMED)) {
			return showSignUp(currentUser, new User(), model);
		} else {
			return "errors/401"; 
		}
	}
	
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String confirmSignUp(@CurrentUser User currentUser, @ModelAttribute @Valid User user,
			BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Model model) {
		currentUser.setName(user.getName());
		
		if(bindingResult.hasErrors()) {
			return showSignUp(currentUser, user, model);
		}
		
		try {
			//userService.confirmUser(currentUser);
			//SecurityUtils.reloadUserDetails(currentUser, request, response);
			return "redirect:/@" + user.getName() + "/urls";
		} catch(DataIntegrityViolationException e) {
			bindingResult.rejectValue("name", "user.error.nameExists", 
					new String[] {user.getName()}, e.getMessage());
			return showSignUp(currentUser, user, model);
		}
	}
	
	
	@RequestMapping(value="/api/users/current", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody User getCurrentUserName(@CurrentUser User currentUser) {
		return currentUser;
//		if(currentUser == null) {
//			currentUser = new User();
//		}
//		return currentUser;
	}
	
	private String showSignUp(User currentUser, User user, Model model) {
		model.addAttribute("unconfirmedUser", currentUser);
		model.addAttribute("user", user);
		return "signup";
	}
}
