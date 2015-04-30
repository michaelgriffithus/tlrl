package com.gnoht.tlrl.config;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.gnoht.tlrl.controller.FiltersHandlerMethodArgumentResolver;
import com.gnoht.tlrl.controller.TargetUserHandlerMethodArgumentResolver;
import com.gnoht.tlrl.security.CurrentUserHandlerMethodArgumentResolver;
import com.gnoht.tlrl.service.UserService;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter implements InitializingBean {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final Logger LOG = LoggerFactory.getLogger(MvcConfig.class);
	
	@Resource private Environment env;
	@Resource private WebApplicationContext webApplicationContext;
	@Resource private UserService userService;

	private String viewPrefix;
	private String viewSuffix;
	private Pattern pattern;
	
	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new CurrentUserHandlerMethodArgumentResolver());
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
		//new ViewControllerRegistryConfigurer(registry)
		//	.configure("urls", "search");
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		viewPrefix = env.getProperty("spring.view.prefix");
		viewSuffix = env.getProperty("spring.view.suffix");
		// builds the following pattern ".*/WEB-INF/views((.*/)(.*)).jsp"
		pattern = Pattern.compile(".*".concat(viewPrefix).concat("((.*/)(.*))").concat(viewSuffix));
	}

	private class ViewControllerRegistryConfigurer {
		ViewControllerRegistry registry;
		
		public ViewControllerRegistryConfigurer(ViewControllerRegistry registry) {
			this.registry = registry;
		}
		
		public ViewControllerRegistryConfigurer configure(String groupName, String ... aliases) {
			String groupPath = webApplicationContext.getServletContext()
					.getRealPath(viewPrefix).concat(FILE_SEPARATOR).concat(groupName);
			
			File groupDirectory = new File(groupPath);
			if(groupDirectory.exists()) {
				configureGroup(groupDirectory, aliases);
			}
			return this;
		}
		
		private void configureGroup(File groupDirectory, String ... aliases) {
			if(groupDirectory.isFile()) {
				registerView(groupDirectory, aliases);
			} else if(groupDirectory.isDirectory()) {
				for(File listing: groupDirectory.listFiles()) {
					configureGroup(listing, aliases);
				}
			}
		}
		
		private void registerView(File file, String ... aliases) {
			try {
				Matcher matcher = pattern.matcher(file.getCanonicalPath());
				if(matcher.matches()) {
					String fileNameWithoutExt = matcher.group(3);
					String groupPath = matcher.group(2);
					String view = matcher.group(1);
					// register default view
					if("index".equalsIgnoreCase(fileNameWithoutExt)) {
						registry.addViewController("/".concat(groupPath)).setViewName(view);
						for(int i=0; i < aliases.length; i++) {
							registry.addViewController("/".concat(aliases[i])).setViewName(view);
						}
						registry.addViewController(groupPath.substring(0, groupPath.length()-1)).setViewName(view);
					} else {
						registry.addViewController(view).setViewName(view);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
