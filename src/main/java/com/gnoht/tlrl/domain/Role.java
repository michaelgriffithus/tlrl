package com.gnoht.tlrl.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.gnoht.tlrl.domain.support.Managed;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="tlrl_role")
public class Role extends Managed<String> {

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
	public void setId(String id) {
		this.id = id;
	}
	@Deprecated
	public Role update(Role from) {
		return this;
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper();
	}
}
