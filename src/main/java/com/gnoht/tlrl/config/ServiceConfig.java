package com.gnoht.tlrl.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.gnoht.tlrl.service.ServicePackage;

@Configuration
@ComponentScan(basePackageClasses={ServicePackage.class})
public class ServiceConfig {

	@Resource private Environment env;
	
	@Resource 
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	@Bean(name="restTemplate")
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(jacksonMessageConverter);
		return restTemplate;
	}
	
}
