package com.gnoht.tlrl.domain.support;

import java.io.Serializable;
import java.util.Date;

/**
 * A {@link Manageable} entity that has auditable attributes.
 * 
 * @param <ID> id of {@link Managed} being audited.
 */
public interface Auditable<ID extends Serializable>
		extends Manageable<ID> {

	/**
	 * @return the date this entity was created.
	 */
	Date getDateCreated();
	
	/**
	 * @return the date this entity was last modified.
	 */
	Date getDateModified();
}
