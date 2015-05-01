package com.gnoht.tlrl.test.config;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.OAuth2SecurityConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.SecurityConfig;
import com.gnoht.tlrl.config.WebMvcConfig;
import com.gnoht.tlrl.service.ReadLaterService;
import com.gnoht.tlrl.service.ReadLaterWebPageService;
import com.gnoht.tlrl.service.RememberMeTokenService;
import com.gnoht.tlrl.service.UserService;

@Configuration
@EnableAutoConfiguration
@Import({ApplicationConfig.class, SecurityConfig.class, 
	OAuth2SecurityConfig.class, WebMvcConfig.class, RepositoryConfig.class})
public class MockApplicationForControllerTesting {
	
	public static void main(String[] args) {
		SpringApplication.run(MockApplicationForControllerTesting.class, args);
	}
	
	@Mock ReadLaterService bookmarkService;
	@Mock UserService userService;
	@Mock RememberMeTokenService rememberMeTokenService;
	@Mock ReadLaterWebPageService readLaterWebPageService;
	
	public MockApplicationForControllerTesting() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Bean
	public UserService userService() {
		return userService;
	}
	
	@Bean
	public ReadLaterService bookmarkService() {
		return bookmarkService;
	}
	
	@Bean
	public RememberMeTokenService rememberMeTokenService() {
		return rememberMeTokenService;
	}
	
	@Bean 
	public ReadLaterWebPageService readLaterWebPageService() {
		return readLaterWebPageService;
	}

}
