package com.gnoht.tlrl.controller.support;

import java.util.Collection;
import java.util.Collections;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response for invalid request that wraps the returned {@link FieldError}s.
 */
public class ErrorResponse extends 
			AbstractApiResponse<Collection<FieldError>> {

	private Collection<FieldError> errors;
	
	public ErrorResponse(String uri, Collection<FieldError> errors) {
		super(uri);
		this.errors = errors;
	}
	
	public ErrorResponse(String uri) {
		this(uri, Collections.<FieldError>emptyList());
	}
	
	public void addError(FieldError error) {
		errors.add(error);
	}
	
	public void addError(String field, String msg) {
		errors.add(new FieldError(field, msg));
	}
	
	@JsonProperty("errors")
	@Override
	public Collection<FieldError> getData() {
		return errors;
	}

}
