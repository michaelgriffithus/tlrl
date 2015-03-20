package com.gnoht.tlrl.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gnoht.tlrl.domain.support.Managed;

/**
 * A descriptive tag used to categorize a {@link Bookmark}. Tags are 
 * case in-sensitive and stored as all lower case.
 */
@Entity(name="tlrl_tag")
public class Tag extends Managed<String> 
		implements Comparable<Tag> {

	private static final long serialVersionUID = 1L;

	@Id @Column(unique=true, nullable=false, updatable=false)
	private String id;

	@SuppressWarnings("unused")
	private Tag() {}
	
	public Tag(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@JsonIgnore
	@Override
	public boolean isNew() {
		return super.isNew();
	}

	@Override
	public int compareTo(Tag other) {
		return id.compareToIgnoreCase(other.getId());
	}

	/**
	 * Make sure tags are lowercase before saving.
	 */
	@PrePersist
	public void lowerCaseId() {
		this.id = id.toLowerCase();
	}
}
