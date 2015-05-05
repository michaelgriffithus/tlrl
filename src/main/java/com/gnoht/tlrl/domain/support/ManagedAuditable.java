package com.gnoht.tlrl.domain.support;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Abstract class for {@link Managed} {@link Auditable}s. Ties auditable
 * attributes to life-cycle events.
 * 
 * @param <ID> id of {@link Managed} being audited.
 */
@MappedSuperclass
public abstract class ManagedAuditable<ID extends Serializable> 
		extends Managed<ID> implements Auditable<ID> {
	
	private static final long serialVersionUID = 1L;

	@Column(nullable=false, updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date dateCreated;
	
	@Column(nullable=false, updatable=true)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date dateModified;
	
	@Override
	public Date getDateCreated() {
		return dateCreated;
	}
	@Override
	public Date getDateModified() {
		return dateModified;
	}

	@PrePersist
	public void prePersist() {
		this.dateCreated = new Date();
		this.dateModified = new Date();
	}
	@PreUpdate
	public void preUpdate() {
		this.dateModified = new Date();
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("dateCreated", dateCreated)
			.add("dateModified", dateModified);
	}
}
