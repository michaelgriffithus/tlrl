package com.gnoht.tlrl.test.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.OAuth2SecurityConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.SecurityConfig;
import com.gnoht.tlrl.config.WebMvcConfig;

@Configuration
@EnableAutoConfiguration
@Import({ApplicationConfig.class, SecurityConfig.class, 
	OAuth2SecurityConfig.class, WebMvcConfig.class, RepositoryConfig.class})
public class MockApplicationWithSecurity {

}
