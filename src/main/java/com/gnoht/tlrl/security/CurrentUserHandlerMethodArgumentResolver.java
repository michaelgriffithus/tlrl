package com.gnoht.tlrl.security;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.gnoht.tlrl.domain.User;

public class CurrentUserHandlerMethodArgumentResolver implements
		HandlerMethodArgumentResolver {

	private static final Logger LOGGER = 
			LoggerFactory.getLogger(CurrentUserHandlerMethodArgumentResolver.class);
	
	@Override
	public Object resolveArgument(MethodParameter methodParameter,
			ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest,
			WebDataBinderFactory webDataBinderFactory) throws Exception {
		
		if(!supportsParameter(methodParameter)) {
			return WebArgumentResolver.UNRESOLVED;
		}
		Principal userPrincipal = nativeWebRequest.getUserPrincipal();
		if(userPrincipal != null) {
			User user = (User) ((Authentication) nativeWebRequest.getUserPrincipal()).getPrincipal();
			LOGGER.debug("Resolved user argument for user: {}", user.getName());
			return user;
		}
		return null;
	}

	@Override
	public boolean supportsParameter(MethodParameter methodParameter) {
		return methodParameter.getParameterAnnotation(CurrentUser.class) != null
				&& methodParameter.getParameterType().equals(User.class);
	}

}
