package com.gnoht.tlrl.domain.support;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Abstract {@link Manageable} with some helper methods for handling {@link #toString()}
 * and default implementations of {@link #equals(Object)} and {@link #hashCode()}.
 * 
 * @param <ID> Identifier of this {@link Managed} instance.
 */
@MappedSuperclass
public abstract class Managed<ID extends Serializable> 
		implements Manageable<ID>, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * See Guava's <a href="https://code.google.com/p/guava-libraries/issues/detail?id=1239">ToStringHelper</a> 
	 * example for details on how to use this pattern.
	 *  
	 * @return {@link ToStringHelper} for building toString. 
	 */
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
			.add("id", getId())
			.add("isNew", isNew());
  }
	
	/**
	 * Default behavior considers an entity new if it has no ID.
	 */
	@Override
	public boolean isNew() {
		return getId() == null;
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

	/**
	 * Identity is determined if two entities have the same ID.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("unchecked")
		Manageable<ID> other = (Manageable<ID>) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}
}
