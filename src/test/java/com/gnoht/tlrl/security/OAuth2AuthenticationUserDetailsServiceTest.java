package com.gnoht.tlrl.security;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.UserService;

public class OAuth2AuthenticationUserDetailsServiceTest {

	@Mock 
	UserService userService;
	
	@InjectMocks
	OAuth2AuthenticationUserDetailsService userDetailsService = 
		new OAuth2AuthenticationUserDetailsService();
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldReturnUserByGivenAuthentication() {
		// given
		User user = new User(1L, "john", "user@xyz.com", SecurityUtils.ROLE_USER, true);
		when(userService.findByEmail(any(String.class))).thenReturn(user);
		OAuth2Authentication auth = new OAuth2Authentication("user@xyz.com");
		
		// when
		OAuth2UserDetails userDetails = (OAuth2UserDetails) userDetailsService.loadUserDetails(auth);
		
		// then
		assertEquals("Not the same user was returned", userDetails.getEmail(), user.getEmail());
		assertTrue(userDetails.getAuthorities().contains(SecurityUtils.asGrantedAuthority(SecurityUtils.ROLE_USER)));
	}
	
	@Test
	public void shouldReturnAnUnconfirmedUser() {
		// given
		User unconfirmedUser = new User(1L, "john", "user@xyz.com", SecurityUtils.ROLE_UNCONFIRMED, true);
		when(userService.findByEmail(any(String.class))).thenReturn(null);
		when(userService.create(any(User.class))).thenReturn(unconfirmedUser);
		OAuth2Authentication auth = new OAuth2Authentication("user@xyz.com");
		
		// when
		OAuth2UserDetails userDetails = 
				(OAuth2UserDetails) userDetailsService.loadUserDetails(auth);
		
		// then
		assertTrue(userDetails.getAuthorities().contains(
				SecurityUtils.asGrantedAuthority(SecurityUtils.ROLE_UNCONFIRMED)));
	}
}
