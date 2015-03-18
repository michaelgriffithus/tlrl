package com.gnoht.tlrl.controller.support;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

/**
 * DTO for Spring {@link org.springframework.validation.FieldError}, that
 * can be consumed by JSON serializer.
 */
public class FieldError implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String field;
	private String message;
	
	public FieldError() {}
	
	public FieldError(String field, String message) {
		this.field = field;
		this.message = message;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("field", field)
			.add("message", message)
			.toString();
	}
}
