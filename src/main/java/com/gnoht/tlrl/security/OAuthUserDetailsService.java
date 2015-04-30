package com.gnoht.tlrl.security;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.UserService;

@Service("userDetailsService")
public class OAuthUserDetailsService implements UserDetailsService,
		AuthenticationUserDetailsService<OAuthAuthenticationToken>{

	@Resource private UserService userService;
	
	/**
	 * Loads the {@link UserDetails} and the underlying {@link User},
	 * from the given {@link Authentication}. If we've made it here,
	 * then authentication was successful with third party OAuth provider.
	 * 
	 * @param auth {@link Authentication} containing authentication details
	 * @return User principal wrapped by OAuthUserDetails 
	 */
	@Override
	public UserDetails loadUserDetails(OAuthAuthenticationToken auth)
			throws UsernameNotFoundException {
		//find the User with given authentication email
		User user = userService.findByEmail(auth.getEmail());
		
		if(user == null) {
			/* User does not exists, but was authenticated, lets create the
			 * user in our system using the authentication details */
			return createUnconfirmedUser(auth);
		}
		
		//if user lookup was successful, lets return User
		return new OAuthUserDetails(user);
	}

	/**
	 * Create a {@link User} with {@link Role} "ROLE_UNCONFIRMED". Unconfirmed
	 * users are user's who've authenticated successfully and known to our system
	 * but have not been confirmed by the User themselves. Confirmation is simply
	 * granting us permission to have them in TLRL system.
	 * 
	 * @param auth
	 * @return
	 */
	protected UserDetails createUnconfirmedUser(OAuthAuthenticationToken auth) {
		//create new unconfirmed User and save
		User unconfirmedUser = new User();
			unconfirmedUser.setEmail(auth.getEmail());
			unconfirmedUser.setName(createTempName(auth.getEmail()));
			unconfirmedUser.setEnabled(false);
			unconfirmedUser.setRole(new Role("ROLE_UNCONFIRMED"));
			
		unconfirmedUser = userService.create(unconfirmedUser);	
			
		return new OAuthUserDetails(unconfirmedUser);
	}

	private String createTempName(String email) {
		return email.replaceAll("(\\W|^_)*", "");
	}
	
	/**
	 * Loads the {@link UserDetails} and underlying {@link User} with
	 * the given user name.
	 * 
	 * @param userName name of User to load.
	 */
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		User user = userService.findByName(userName);
		if(user == null) {
			throw new ManageableNotFoundException();
		}
		return new OAuthUserDetails(user);
	}
}
