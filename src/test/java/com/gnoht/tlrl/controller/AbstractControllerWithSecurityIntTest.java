package com.gnoht.tlrl.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;

import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.OAuth2SecurityConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.SecurityConfig;
import com.gnoht.tlrl.config.WebMvcConfig;
import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.OAuth2Authentication;
import com.gnoht.tlrl.security.OAuth2UserDetails;
import com.gnoht.tlrl.service.BookmarkService;
import com.gnoht.tlrl.service.RememberMeTokenService;
import com.gnoht.tlrl.service.UserService;


public abstract class AbstractControllerWithSecurityIntTest {

	protected Authentication authenticatedWithRole(Role role) {
		User user = new User("testUser", "testUser@xyz.com");
		user.setRole(role);
		user.setEnabled(true);
		OAuth2UserDetails userDetails = new OAuth2UserDetails(user);
		OAuth2Authentication auth = new OAuth2Authentication(userDetails, userDetails);
		return auth;
	}
	
	@Configuration
	@EnableAutoConfiguration
	@Import({ApplicationConfig.class, SecurityConfig.class, 
			OAuth2SecurityConfig.class, WebMvcConfig.class, RepositoryConfig.class })
	public static class MockApplication implements EmbeddedServletContainerCustomizer {
		
		public static void main(String[] args) {
			SpringApplication.run(MockApplication.class, args);
		}
		
		@Mock BookmarkService bookmarkService;
		@Mock UserService userService;
		@Mock RememberMeTokenService rememberMeTokenService;
		
		public MockApplication() {
			MockitoAnnotations.initMocks(this);
		}
		
		@Bean
		public UserService userService() {
			return userService;
		}
		
		@Bean
		public BookmarkService bookmarkService() {
			return bookmarkService;
		}
		
		@Bean
		public RememberMeTokenService rememberMeTokenService() {
			return rememberMeTokenService;
		}
		
		@Override
		public void customize(ConfigurableEmbeddedServletContainer arg0) {}
	}
}
