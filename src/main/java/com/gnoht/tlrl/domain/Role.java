package com.gnoht.tlrl.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="role")
public class Role extends Managed<String, Role> {

	private static final long serialVersionUID = -8267798491874591689L;
	
	@Id 
	private String id;
	
	public Role() {}
	
	public Role(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
	@Override
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public Role update(Role from) {
		return this;
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper();
	}
}
