package com.gnoht.tlrl.controller.support;

import static org.springframework.http.HttpStatus.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gnoht.tlrl.controller.ControllerPackage;

@ControllerAdvice(basePackageClasses={ControllerPackage.class})
public class ErrorHandlerAdvice {

	@Resource
	private MessageSourceAccessor messageSourceAccessor;

	/**
	 * Handles validation errors. Wrapping Spring's {@link BindingResult} {@link FieldError}s
	 * into a serializable DTO.
	 * 
	 * @param request originating request
	 * @param ex caught exception
	 * @return
	 */
	@ExceptionHandler(value=MethodArgumentNotValidException.class)
	@ResponseStatus(value=BAD_REQUEST)
	public @ResponseBody ErrorResponse validationError(
			HttpServletRequest request, MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		ErrorResponse errorResponse = new ErrorResponse(request.getRequestURI());
		for(FieldError error: bindingResult.getFieldErrors()) {
			errorResponse.addError(error.getField(), messageSourceAccessor.getMessage(error));
		}
		return errorResponse;
	}
	
}
