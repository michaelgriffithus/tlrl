package com.gnoht.tlrl.config;

import static org.junit.Assert.*;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import javax.servlet.Filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.OAuth2AuthenticationUserDetailsService;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.RememberMeTokenServiceImpl;
import com.gnoht.tlrl.service.UserService;
import com.gnoht.tlrl.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class SecurityConfigIntTest {

	@Autowired
	private WebApplicationContext webAppContext;
	@Autowired
	private Filter springSecurityFilterChain;
	private MockMvc mockMvc;
	
	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext)
			.apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
			.build();
	}
	
	@Test
	public void shouldRedirectToGoogleForAuthentication() throws Exception {
		mockMvc.perform(get("/auth/google"))
			.andDo(print())
			.andExpect(status().is3xxRedirection())
			.andExpect(new ResultMatcher() {
				@Override
				public void match(MvcResult result) throws Exception {
					assertTrue(result.getResponse().getRedirectedUrl()
						.startsWith("https://accounts.google.com/o/oauth2/auth?client_id="));
				}
			});
	}
	
	@Test
	public void shouldRedirectToSignupView() throws Exception  {
		User unconfirmedUser = new User();
		unconfirmedUser.setEmail("xyz@acme.com");
		unconfirmedUser.setRole(SecurityUtils.ROLE_UNCONFIRMED);
		mockMvc.perform(get("/@").with(user(new OAuth2UserDetails(unconfirmedUser))))
			.andDo(print())
			.andExpect(forwardedUrl("/signup"));
	}
	
}
