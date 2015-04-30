package com.gnoht.tlrl.controller;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gnoht.tlrl.domain.ValidationError;

@ControllerAdvice(basePackageClasses= { DefaultExceptionHandler.class })
public class DefaultExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultExceptionHandler.class);
	
	@Resource(name="messageSourceAccessor")
	private MessageSourceAccessor messageSource;

	@ExceptionHandler(value=MethodArgumentNotValidException.class)
	@ResponseStatus(value=HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ValidationError validationError(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();
		return parseFieldErrors(bindingResult.getFieldErrors());
	}
	
	private ValidationError parseFieldErrors(List<FieldError> fieldErrors) {
		ValidationError validationError = new ValidationError();
		for(FieldError fieldError: fieldErrors) {
			validationError.addFieldError(fieldError.getField(), messageSource.getMessage(fieldError));
		}
		return validationError;
	}
	
	public void setMessageSource(MessageSourceAccessor messageSource) {
		this.messageSource = messageSource;
	}
}
