package com.gnoht.tlrl.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidationError implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<FieldError> fieldErrors = new ArrayList<FieldError>();
	
	public ValidationError() {}
	
	public void addFieldError(String field, String message) {
		fieldErrors.add(new FieldError(field, message));
	}
	public List<FieldError> getFieldErrors() {
		return fieldErrors;
	}
	public void setFieldErrors(List<FieldError> fieldErrors) {
		this.fieldErrors = fieldErrors;
	}
}
