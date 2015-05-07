package com.gnoht.tlrl.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gnoht.tlrl.domain.support.Managed;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="tlrl_user")
public class User extends Managed<Long>{

	private static final long serialVersionUID = -8669637638091978788L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(unique=true)
	@NotEmpty(message="user.error.nameIsEmpty")
	@NotNull(message="user.error.nameIsEmpty")
	@Length(min=3, message="user.error.nameTooShort")
	@Pattern(regexp="^[a-zA-Z0-9_]*$", message="user.error.nameHasInvalidChars")
	private String name;
	
	@JsonIgnore
	@Column(unique=true)
	private String email;

	private boolean enabled;
	
	@ManyToOne(targetEntity=Role.class, fetch=FetchType.EAGER, cascade={CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="role_id", nullable=false)
	private Role role;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
	public static User withName(String name) {
		User user = new User();
		user.name = name;
		return user;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("name", name)
			.add("enabled", enabled)
			.add("email", email)
			.add("role", role);
	}
}
