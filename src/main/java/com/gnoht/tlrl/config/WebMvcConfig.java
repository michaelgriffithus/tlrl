package com.gnoht.tlrl.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.gnoht.tlrl.controller.ControllerPackage;

@Configuration
@ComponentScan(basePackageClasses={ControllerPackage.class})
public class WebMvcConfig extends WebMvcConfigurerAdapter {

}
