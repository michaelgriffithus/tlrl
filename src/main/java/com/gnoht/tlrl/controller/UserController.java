package com.gnoht.tlrl.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.OAuthUserDetails;

@RestController
@RequestMapping(value="/api")
public class UserController {

//	@RequestMapping(value="/users/current")
//	public OAuthUserDetails currentUser() {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		System.out.println("====================");
//		System.out.println(auth);
//		return (OAuthUserDetails) auth.getPrincipal();
//	}
}
