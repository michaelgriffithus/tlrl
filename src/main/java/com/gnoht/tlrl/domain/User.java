package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gnoht.tlrl.domain.support.Managed;
import com.gnoht.tlrl.security.SecurityUtils;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Class representing a user within the application.
 */
@Entity(name="tlrl_user")
public class User extends Managed<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
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

	private boolean enabled = false;
	
	@ManyToOne(targetEntity=Role.class, fetch=FetchType.EAGER, cascade={CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="role_id", nullable=false)
	private Role role;

//	@JsonIgnore
//	@OneToMany(targetEntity=OAuthToken.class, fetch=FetchType.EAGER)
//	@JoinColumn(name="user_id", referencedColumnName="id")
//	private List<OAuthToken> oauthTokens = new ArrayList<OAuthToken>();

	public User() {}
	
	public User(Long id) {
		this.id = id;
	}

	public User(Long id, String name, String email, Role role, boolean enabled) {
		this(id);
		this.name = name;
		this.email = email;
		this.role = role;
		this.enabled = enabled;
	}
	
	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	@Override
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getEmail() {
		return email;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public Role getRole() {
		return role;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("name", name)
			.add("enabled", enabled)
			.add("role", role);
	}
}
