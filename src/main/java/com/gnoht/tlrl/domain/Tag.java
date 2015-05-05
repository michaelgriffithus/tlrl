package com.gnoht.tlrl.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gnoht.tlrl.domain.support.Managed;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="tag")
public class Tag extends Managed<String>
		implements Comparable<Tag> {

	private static final long serialVersionUID = -8835168755690542165L;
	
	@Id @Column(unique=true)
	private String id;
	
	@Transient
	private Integer count;
	
	public Tag() {}
	
	public Tag(String tagId) {
		this.id = tagId;
	}
	public Tag(String tagId, int count) {
		this.id = tagId;
		this.count = count;
	}
	
	@JsonInclude(Include.NON_NULL)
	public Integer getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	@Deprecated
	public Tag update(Tag from) {
		return null;
	}
	
	@Override
	public int compareTo(Tag other) {
		return id.compareToIgnoreCase(other.id);
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("count", count);
	}
}
