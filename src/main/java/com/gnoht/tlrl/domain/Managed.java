package com.gnoht.tlrl.domain;

import java.io.Serializable;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public abstract class Managed<ID extends Serializable, T extends Manageable<ID, T>> 
		implements Manageable<ID, T>, Serializable {
	
	private static final long serialVersionUID = 6099447223349442285L;

	/**
	 * See Guava's <a href="https://code.google.com/p/guava-libraries/issues/detail?id=1239">ToStringHelper</a> 
	 * example for details on how to use this pattern.
	 *  
	 * @return {@link ToStringHelper} for building toString. 
	 */
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
			.add("id", getId());
  }

	@Override
	public final String toString() {
		return toStringHelper().toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Manageable<ID,T> other = (Manageable<ID,T>) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
