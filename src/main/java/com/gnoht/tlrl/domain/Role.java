package com.gnoht.tlrl.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.gnoht.tlrl.domain.support.Managed;

/**
 * Class representing a role within the application (e.g. ROLE_USER).
 */
@Entity(name="tlrl_role")
public class Role extends Managed<String> {

	private static final long serialVersionUID = -8267798491874591689L;
	
	@Id private String id;
	
	public Role() {}
	
	public Role(String id) {
		this.id = id;
	}
	
	@Override
	public String getId() {
		return id;
	}
}
