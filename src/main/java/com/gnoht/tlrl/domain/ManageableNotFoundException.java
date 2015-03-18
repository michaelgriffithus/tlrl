package com.gnoht.tlrl.domain;

import java.io.Serializable;

/**
 * Exception thrown when {@link ManageableNotFoundException} with 
 * given id is not found.
 */
public class ManageableNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Serializable id;

	public ManageableNotFoundException(Serializable id) {
		super("Manageable with id: " + id + ", not found!");
		this.id = id;
	}

	public Serializable getId() {
		return id;
	}
}
