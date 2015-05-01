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

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.OAuth2SecurityConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.config.WebMvcConfig;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.ReadLaterWebPageSolrRepository;
import com.gnoht.tlrl.security.OAuth2AuthenticationUserDetailsService;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.ReadLaterService;
import com.gnoht.tlrl.service.ReadLaterWebPageService;
import com.gnoht.tlrl.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class WebAppControllerIntTest {

	@Autowired WebApplicationContext context;
	
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
	public void shouldRedirectToUserHome() throws Exception {
		User user = new User();
		user.setId(1L);
		user.setName("thong");
		user.setEmail("thong@xyz.com");
		user.setRole(SecurityUtils.ROLE_USER);
		user.setEnabled(true);

		mockMvc.perform(get("/").with(user(new OAuth2UserDetails(user))))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/@" + user.getName()))
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

}
