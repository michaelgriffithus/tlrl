package com.gnoht.tlrl.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.gnoht.tlrl.service.ServicePackage;

@Configuration
@ComponentScan(basePackageClasses={ServicePackage.class})
public class ServiceConfig {

}
