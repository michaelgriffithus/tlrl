package com.gnoht.tlrl.domain;

import org.springframework.dao.DuplicateKeyException;

public class AlreadySignedUpException extends DuplicateKeyException {

	private static final long serialVersionUID = 1L;
	
	public AlreadySignedUpException(String msg) {
		super(msg);
	}
}
