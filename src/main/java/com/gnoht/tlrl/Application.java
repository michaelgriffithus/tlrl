package com.gnoht.tlrl;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.ApplicationPidFileWriter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration(exclude={SecurityAutoConfiguration.class})
@ComponentScan(basePackages={"com.gnoht.tlrl"})
public class Application extends SpringBootServletInitializer { 

	public static void main(String[] args) {
		SpringApplication springApplication = new SpringApplication(Application.class);
//		if(!System.getenv("ACTIVE_PROFILE").equalsIgnoreCase("dev")) {
//			String pidFilePath = System.getenv("TLRL_HOME").concat(File.pathSeparator)
//				.concat(System.getenv("TLRL_USER")).concat(".pid");
//			springApplication.addListeners(new ApplicationPidFileWriter(pidFilePath));
//		}
		springApplication.addListeners(new ApplicationPidFileWriter("/opt/tlrl/tlrl.pid"));
		springApplication.run(args);
		
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
	
}
