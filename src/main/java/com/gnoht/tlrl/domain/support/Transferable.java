package com.gnoht.tlrl.domain.support;

import java.io.Serializable;

/**
 * Data transfer object corresponding to a {@link Manageable} instance. 
 */
public interface Transferable<T extends Manageable<?>> extends Serializable {
	
	T toManageable();
}
