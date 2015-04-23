package com.gnoht.tlrl.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.keygen.KeyGenerators;

import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;

public final class SecurityUtils {
	
	public static final Role ROLE_USER = new Role("ROLE_USER");
	public static final Role ROLE_UNCONFIRMED = new Role("ROLE_UNCONFIRMED");

	/**
	 * Generates a secure random String.
	 * @return
	 */
	public static String secureRandomStringKey() {
		return KeyGenerators.string().generateKey();
	}
	
	/**
	 * Converts given {@link Role} to a {@link GrantedAuthority} to be used in
	 * Spring Security context.
	 * 
	 * @param role
	 * @return
	 */
	public static GrantedAuthority asGrantedAuthority(Role role) {
		return new SimpleGrantedAuthority(role.getId());
	}
	
	/**
	 * Checks if given principal (either instance of User or Authentication) 
	 * has the given {@link Role}.
	 *  
	 * @param principal instance of either {@link User} or {@link Authentication}
	 * @param role {@link Role} to check for
	 * @return 
	 */
	public static boolean hasRole(Object principal, Role role) {
		if(principal != null) {
			if(principal instanceof User && ((User)
					principal).getRole().equals(role)) {
				return true;
			}
			if(principal instanceof Authentication && ((Authentication) 
					principal).getAuthorities().contains(asGrantedAuthority(role))) {
				return true;
			}
		}
		return false;
	}
	
}
