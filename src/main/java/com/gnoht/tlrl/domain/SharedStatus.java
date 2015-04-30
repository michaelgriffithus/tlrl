package com.gnoht.tlrl.domain;

/**
 * Maintains state for "shared" attribute.
 */
public enum SharedStatus {

	NA,
	PRIVATE,
	PUBLIC(true);
	
	private boolean status = false;
	
	SharedStatus() {}
	
	SharedStatus(boolean status) {
		this.status = status;
	}
	
	public boolean status() {
		return status;
	}
}
