package com.gnoht.tlrl.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gnoht.tlrl.domain.User;
import com.google.common.base.MoreObjects.ToStringHelper;

public class OAuth2UserDetails extends User 
		implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Collection<GrantedAuthority> authorities = new ArrayList<>();
	
	public OAuth2UserDetails(User user) {
		super(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isEnabled());
		authorities.add(new SimpleGrantedAuthority(getRole().getId()));
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.unmodifiableCollection(authorities);
	}

	@Override
	public String getPassword() {
		return SecurityUtils.secureRandomStringKey();
	}

	@Override
	public String getUsername() {
		return getName();
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
