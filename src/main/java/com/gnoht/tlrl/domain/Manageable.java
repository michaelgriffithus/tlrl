package com.gnoht.tlrl.domain;

import java.io.Serializable;

/**
 * Base class for domain objects that are managed in a repository.
 */
public interface Manageable<ID extends Serializable, T extends Manageable<ID,T>> {

	public ID getId();
	public void setId(ID id);
	public T update(T from);
}
