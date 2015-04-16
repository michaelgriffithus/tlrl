package com.gnoht.tlrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import com.gnoht.tlrl.config.ConfigPackage;

@SpringBootApplication // @Configuration, @EnableAutoConfiguration, @ComponentScan
@ComponentScan(basePackageClasses={ConfigPackage.class})
public class Application implements EmbeddedServletContainerCustomizer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void customize(ConfigurableEmbeddedServletContainer container) {
	}
}
