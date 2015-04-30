package com.gnoht.tlrl.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gnoht.tlrl.domain.User;
import com.google.common.base.MoreObjects.ToStringHelper;

public class OAuthUserDetails 
		extends User implements UserDetails {

	private static final long serialVersionUID = 5938803981040019080L;

	private List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	
	public OAuthUserDetails(User user) {
		this.setId(user.getId());
		this.setEmail(user.getEmail());
		this.setEnabled(user.isEnabled());
		this.setName(user.getName());
		this.setRole(user.getRole());
		authorities.add(new SimpleGrantedAuthority(getRole().getId()));
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return this.getName();
	}

	@Override public boolean isAccountNonExpired() { return true; }
	@Override public boolean isAccountNonLocked() { return true; }
	@Override public boolean isCredentialsNonExpired() { return true; }

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("username", getUsername())
			.add("isAccountNonExpired", isAccountNonExpired())
			.add("isAccountNonLocked", isAccountNonLocked())
			.add("isCredentialsNonExpired", isCredentialsNonExpired())
			.add("authorities", authorities);
	}
}
