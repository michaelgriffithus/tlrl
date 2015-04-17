package com.gnoht.tlrl.security;

import static com.gnoht.tlrl.security.SecurityUtils.*;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.UserService;

/**
 * {@link UserDetailsService} implementation that loads a {@link User} via 
 * {@link UserDetails} from a given {@link OAuth2Authentication}. It's assume 
 * the passed in Authentication has been authenticated by an 
 * {@link OAuth2AuthenticationTokenService} and contains an valid user email. 
 * If the passed in Authentication does not have an associated User, one will 
 * be created.
 */
@Service("userDetailsService")
public class OAuth2AuthenticationUserDetailsService 
		implements UserDetailsService, AuthenticationUserDetailsService<OAuth2Authentication> {

	protected static final Logger LOG = LoggerFactory.getLogger(OAuth2AuthenticationUserDetailsService.class);
	
	@Resource private UserService userService;
	
	@Override
	public UserDetails loadUserDetails(OAuth2Authentication auth)
			throws UsernameNotFoundException {
		
		LOG.info("Starting loadUserDetails(): auth={}", auth);

		/* At this point, request has been authenticated against OAuth provider
		 * and we have an email identifying the authenticated principal. 
		 * It's just a matter of looking up that email and it's associated User
		 * or if it's their first time to our system, create a new account. */
		User user = userService.findByEmail((String) auth.getPrincipal());
		if(user == null) {
			// user doesn't exists in our system yet, but they've been authenticated
			// let's create a new account, with "UNCONFIRMED" role. 
			return createUnconfirmedUser(auth);
		} else {
			return new OAuth2UserDetails(user);
		}
	}

	/**
	 * Creates a new User from given {@link OAuth2Authentication}. The only requirement
	 * for a User to be created is a valid email address, which is the principal of given
	 * authentication object.
	 * 
	 * @param auth Authenticated principal (with email)
	 * @return
	 */
	private OAuth2UserDetails createUnconfirmedUser(OAuth2Authentication auth) {
		LOG.info("Starting createUnconfirmedUser(): auth={}", auth);
		User unconfirmedUser = userService.create(
			new User(
				secureRandomStringKey(), //TODO: possible constraint violation?
				(String) auth.getPrincipal(), // email is stored as principal
				ROLE_UNCONFIRMED, 
				false));
		
		return new OAuth2UserDetails(unconfirmedUser);
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return null;
	}

}
