package com.gnoht.tlrl.controller;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
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
import com.gnoht.tlrl.test.config.MockApplicationForControllerTesting;

public abstract class AbstractControllerWithSecurityIntTest {

	protected Authentication authenticatedWithRole(Role role) {
		User user = new User();
		user.setName("testUser");
		user.setEmail("testUser@xyz.com");
		user.setRole(role);
		user.setEnabled(true);
		OAuth2UserDetails userDetails = new OAuth2UserDetails(user);
		OAuth2Authentication auth = new OAuth2Authentication(userDetails, userDetails);
		return auth;
	}

}
