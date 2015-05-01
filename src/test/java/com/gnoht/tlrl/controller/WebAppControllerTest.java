package com.gnoht.tlrl.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.config.WebMvcConfig;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.OAuth2AuthenticationUserDetailsService;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.UserService;

public class WebAppControllerTest 
		extends StandaloneControllerTest<WebAppController> {

	@Autowired WebApplicationContext context;
	
	@Autowired UserDetailsService userDetailsService;
	
	
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(SecurityMockMvcConfigurers.springSecurity())
			.alwaysDo(print())
			.build();
	}

	@Test
	@WithUserDetails("thong")
	public void shouldRedirectToUserHome() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/@thong"))
			.andReturn();
	}
	
	@Test
	public void shouldReturnDefaultHome() throws Exception {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.build();
		
		mockMvc.perform(get("/").with(authentication(null)))
			.andExpect(status().isOk())
			.andExpect(view().name("index"))
			.andReturn();
	}
	
	@Configuration
	public static class SecurityConfig {
		
		@Mock UserService userService;
		
		@Bean
		public UserService userService() {
			return userService;
		}
		
		@Bean
		public UserDetailsService userDetailsService() {
			UserDetailsService userDetailsService = Mockito.mock(OAuth2AuthenticationUserDetailsService.class);
			when(userDetailsService.loadUserByUsername(any(String.class))).then(new Answer<OAuth2UserDetails>() {
				@Override
				public OAuth2UserDetails answer(InvocationOnMock invocation)
						throws Throwable {
					User user = new User();
					user.setId(1L);
					user.setName("thong");
					user.setEmail("thong@xyz.com");
					user.setRole(SecurityUtils.ROLE_USER);
					user.setEnabled(true);
					return new OAuth2UserDetails(user);
				}
			});
					
			return userDetailsService;
		}
	}

	@Override
	protected WebAppController createController() {
		return new WebAppController();
	}
}
