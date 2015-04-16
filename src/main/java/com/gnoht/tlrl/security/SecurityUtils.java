package com.gnoht.tlrl.security;

import org.springframework.security.crypto.keygen.KeyGenerators;

public final class SecurityUtils {
	public static String secureRandomStringKey() {
		return KeyGenerators.string().generateKey();
	}
}
