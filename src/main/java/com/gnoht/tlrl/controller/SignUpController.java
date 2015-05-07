package com.gnoht.tlrl.controller;

import static com.gnoht.tlrl.security.SecurityUtils.ROLE_UNCONFIRMED;
import static com.gnoht.tlrl.security.SecurityUtils.hasRole;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gnoht.tlrl.config.SecurityConfig;
import com.gnoht.tlrl.domain.AlreadySignedUpException;
import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.security.OAuth2Authentication;
import com.gnoht.tlrl.security.OAuth2AuthenticationUserDetailsService;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.service.RememberMeTokenService;
import com.gnoht.tlrl.service.UserService;

/**
 * Controller for managing sign up actions.
 */
@Controller
public class SignUpController {

	private static final Logger LOG = LoggerFactory.getLogger(SignUpController.class);
	
	@Resource private UserService userService;
	@Resource private OAuth2AuthenticationUserDetailsService userDetailsService;
	@Resource private RememberMeServices rememberMeServices;
	
	@Resource(name="rememberMeTokenService")
	private RememberMeTokenService rememberMeTokenService;
	
	/**
	 * Handles request for signing up a {@link User}. Usually we get here by 
	 * accessDeniedHandler redirect. The user is authenticated, but only has 
	 * {@link Role} of "UNCONFIRMED" (which means they're not fully signed up),
	 * show them the sign-up view. If it's a fully authenticated User with 
	 * proper "USER" role, just send them on their way. Everyone else, just 
	 * invalidate their session just in case and send them to sign-in page.
	 * 
	 * @param authUserToSignUp 
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/signup", method=RequestMethod.GET)
	public String signUp(@CurrentUser User authUserToSignUp, Model model) {
		LOG.info("Starting signUp(): authUserToSignUp={}", authUserToSignUp);
		if(hasRole(authUserToSignUp, ROLE_UNCONFIRMED)) {
			return showSignUp(authUserToSignUp, model);
		} else {
			return "redirect:/signout";
		}
	}
	
	/**
	 * Sign up the submitted {@link User}. 
	 * 
	 * @param authUserToSignUp the current User in the security context we trying to sign up
	 * @param newUser command encapsulating the user info needed for signup
	 * @param bindingResult 
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/signup", method=RequestMethod.POST)
	public String doSignUp(@CurrentUser User authUserToSignUp, @ModelAttribute("user") @Valid User newUser,
			BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Model model) {
		LOG.info("Starting doSignUp(): currentUser={}, user={}", authUserToSignUp, newUser);
		
		newUser.setEmail(authUserToSignUp.getEmail());
		
		if(!bindingResult.hasErrors()) {
			try {
				authUserToSignUp = userService.signUpUser(newUser);
				reloadAuthentication(authUserToSignUp, request, response);
				return "redirect:/@" + authUserToSignUp.getName();
				
			} catch(AlreadySignedUpException e) {
				bindingResult.rejectValue("email", "user.error.alreadySignedUp",
						new String[]{authUserToSignUp.getEmail()}, e.getMessage());
			} catch(DataIntegrityViolationException e) {
				bindingResult.rejectValue("name", "user.error.nameExists",
					new String[]{newUser.getName()}, e.getMessage());
			}
		}
		
		return showSignUp(newUser, model);
	}
	
	private String showSignUp(User user, Model model) {
		model.addAttribute("user", user);
		return "signup";
	}
	
	public void reloadAuthentication(User user, 
			HttpServletRequest request, HttpServletResponse response) {
		rememberMeTokenService.removeUserTokens(user.getName());
		OAuth2UserDetails userDetails = (OAuth2UserDetails) 
				userDetailsService.loadUserByUsername(user.getName());
		OAuth2Authentication authToken = 
				new OAuth2Authentication(userDetails, userDetails);
		SecurityContextHolder.getContext().setAuthentication(authToken);
		rememberMeServices.loginSuccess(request, response, authToken);
	}
	
}
