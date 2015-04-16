package com.gnoht.tlrl.security;

import org.springframework.security.crypto.keygen.KeyGenerators;

import com.gnoht.tlrl.domain.Role;

public final class SecurityUtils {
	
	public static final Role UNCONFIRMED_ROLE = new Role("ROLE_UNCONFIRMED");

	public static String secureRandomStringKey() {
		return KeyGenerators.string().generateKey();
	}
}
