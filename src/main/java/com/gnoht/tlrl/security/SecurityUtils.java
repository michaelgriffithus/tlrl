package com.gnoht.tlrl.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.keygen.KeyGenerators;

import com.gnoht.tlrl.domain.Role;

public final class SecurityUtils {
	
	public static final Role ROLE_USER = new Role("ROLE_USER");
	public static final Role ROLE_UNCONFIRMED = new Role("ROLE_UNCONFIRMED");

	public static String secureRandomStringKey() {
		return KeyGenerators.string().generateKey();
	}
	
	public static GrantedAuthority asGrantedAuthority(Role role) {
		return new SimpleGrantedAuthority(role.getId());
	}
}
