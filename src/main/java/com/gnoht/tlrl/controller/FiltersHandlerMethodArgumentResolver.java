package com.gnoht.tlrl.controller;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class FiltersHandlerMethodArgumentResolver 
		implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(FiltersArgument.class) &&
				parameter.getParameterType().equals(Filters.class);
	}

	protected String getParameterName(MethodParameter parameter) {
		return parameter.getParameterAnnotation(FiltersArgument.class).value();
	}
	
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		
		if(supportsParameter(parameter)) {
			String filtersParameter = webRequest.getParameter(getParameterName(parameter));
			if(filtersParameter != null) {
				return new Filters(filtersParameter.split(","));
			}
		}
		
		return new Filters();
	}

}
