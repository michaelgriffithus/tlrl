package com.gnoht.tlrl.domain.support;

import java.io.Serializable;

/**
 * Represents a managed entity within the application.
 * 
 * @param <ID> Identifier of this {@link Manageable}
 */
public interface Manageable<ID extends Serializable> {

	/**
	 * @return returns the id of this {@link Manageable}
	 */
	ID getId();
	
	/**
	 * @return boolean indicating if this {@link Manageable} is new.
	 */
	boolean isNew();
}
