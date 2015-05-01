package com.gnoht.tlrl.config;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.gnoht.tlrl.controller.ControllerPackage;
import com.gnoht.tlrl.controller.FiltersHandlerMethodArgumentResolver;
import com.gnoht.tlrl.controller.support.TargetUserHandlerMethodArgumentResolver;
import com.gnoht.tlrl.service.UserService;

@Configuration
@ComponentScan(basePackageClasses={ ControllerPackage.class })
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Resource private Environment env;
	@Resource private UserService userService;

	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new TargetUserHandlerMethodArgumentResolver(userService));
		argumentResolvers.add(new FiltersHandlerMethodArgumentResolver());
		super.addArgumentResolvers(argumentResolvers);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		super.addResourceHandlers(registry);
		registry.addResourceHandler("/static/**")
			.addResourceLocations("/static/")
			.setCachePeriod(31556926);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		super.addViewControllers(registry);
		registry.addViewController("/about").setViewName("about");
		registry.addViewController("/help").setViewName("help");
	}
}
