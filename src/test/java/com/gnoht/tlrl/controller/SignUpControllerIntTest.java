package com.gnoht.tlrl.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gnoht.tlrl.domain.AlreadySignedUpException;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={AbstractControllerWithSecurityIntTest.MockApplication.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class SignUpControllerIntTest extends AbstractControllerWithSecurityIntTest {

	@Autowired WebApplicationContext context;
	@Autowired Filter springSecurityFilterChain;
	@Autowired UserService userService;
	
	private MockMvc mockMvc;
	
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
			.build();
		
		Mockito.reset(userService);
	}
	
	@Test
	public void signUpShouldRedirectToSignOut() throws Exception {
		mockMvc.perform(get("/signup").with(authentication(null)))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("http://localhost/"));
	}
	
	@Test
	public void signUpShouldShowSignUpView() throws Exception {
		Authentication auth = authenticatedWithRole(SecurityUtils.ROLE_UNCONFIRMED); 
		mockMvc.perform(get("/signup").with(authentication(auth)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name("signup"))
			.andExpect(model().attribute("user", auth.getDetails()));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void doSignUpShouldFailWithAlreadySignedUpException() throws Exception {
		Authentication auth = authenticatedWithRole(SecurityUtils.ROLE_UNCONFIRMED);
		when(userService.signUpUser(any(User.class)))
			.thenThrow(AlreadySignedUpException.class);
		
		mockMvc.perform(post("/signup")
				.param("name", "blah")
				.with(authentication(auth)))
			.andDo(print())
			.andExpect(model().attributeHasFieldErrorCode("user", "email", "user.error.alreadySignedUp"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void doSignUpShouldFailWithDataIntegrityException() throws Exception {
		Authentication auth = authenticatedWithRole(SecurityUtils.ROLE_UNCONFIRMED);
		when(userService.signUpUser(any(User.class)))
			.thenThrow(DataIntegrityViolationException.class);
		
		mockMvc.perform(post("/signup")
				.param("name", "blah")
				.with(authentication(auth)))
			.andDo(print())
			.andExpect(model().attributeHasFieldErrorCode("user", "name", "user.error.nameExists"));
	}
	
	@Test
	public void doSignUpShouldSucceedAndRedirectToUserHome() throws Exception {
		Authentication auth = authenticatedWithRole(SecurityUtils.ROLE_UNCONFIRMED);
		when(userService.signUpUser(any(User.class))).then(new Answer<User>() {
			@Override public User answer(InvocationOnMock invocation) throws Throwable {
				User user = (User) invocation.getArguments()[0];
				user.setRole(SecurityUtils.ROLE_USER);
				return user;
			}
		});
		when(userService.findByName(any(String.class)))
			.thenReturn((OAuth2UserDetails) auth.getDetails());
		
		mockMvc.perform(post("/signup")
					.param("name", auth.getName())
					.with(authentication(auth)))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/@" + auth.getName()));
	}

}
