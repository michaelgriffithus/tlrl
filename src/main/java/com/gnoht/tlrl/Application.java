package com.gnoht.tlrl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.gnoht.tlrl.config.ConfigPackage;

@Configuration
@EnableAutoConfiguration(exclude={SecurityAutoConfiguration.class})
@ComponentScan(basePackageClasses={ConfigPackage.class})
public class Application { 

	public static void main(String[] args) {
		SpringApplication springApp = new SpringApplication(Application.class);
		/* set via spring.pidfile in application.properties */
		springApp.addListeners(new ApplicationPidFileWriter());
		springApp.run(args);
	}

	@Bean
	EmbeddedServletContainerCustomizer embeddedServletContainerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
			}
		};
	}
	
}
