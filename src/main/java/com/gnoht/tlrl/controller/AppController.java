package com.gnoht.tlrl.controller;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;

//@Controller
public class AppController {

//	@RequestMapping(value={"/app", "/@*/**", "/urls", "/url/**", "/search", "/popular", "/recent"}, method=RequestMethod.GET)
//	public String app(@CurrentUser User currentUser, Model model) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		if(auth.isAuthenticated() && currentUser != null) {
//			model.addAttribute("user", auth.getPrincipal());
//		}
//		return "app";
//	}
//	
//	@RequestMapping(value={"", "/"}, method=RequestMethod.GET)
//	public String home(@CurrentUser User currentUser, HttpServletResponse response) throws IOException {
//		if(currentUser == null) {
//			return "index";
//		} else {
//			return "redirect:/@" + currentUser.getName();
//		}
//	}
	
}
