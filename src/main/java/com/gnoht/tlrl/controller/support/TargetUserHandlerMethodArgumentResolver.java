package com.gnoht.tlrl.controller.support;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.UserService;

public class TargetUserHandlerMethodArgumentResolver 
		implements HandlerMethodArgumentResolver{

	private static final Logger LOG = LoggerFactory.getLogger(TargetUserHandlerMethodArgumentResolver.class);
	
	private UserService userService;
	
	public TargetUserHandlerMethodArgumentResolver(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(TargetUser.class) != null &&
				parameter.getParameterType().equals(User.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		
		if(supportsParameter(parameter)) {
			String targetUserName = resolveUriTemplateName(
					getUriTemplateName(parameter), parameter, webRequest);

			if(targetUserName != null) {
				User user = userService.findByName(targetUserName);
				if(user == null) {
					user = new User();
					user.setName(targetUserName);
				}
				return user;
			}
		}
		return WebArgumentResolver.UNRESOLVED;
	}

	protected String getUriTemplateName(MethodParameter parameter) {
		return parameter.getParameterAnnotation(TargetUser.class).value();
	}
	
	protected String resolveUriTemplateName(String name, MethodParameter parameter,
			NativeWebRequest request) throws Exception {
		LOG.debug("Starting resolveUriTemplateName: {}", name);
		Map<String, String> uriTemplateVars =
				(Map<String, String>) request.getAttribute(
						HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
		return (uriTemplateVars != null) ? uriTemplateVars.get(name) : null;
	}

}
