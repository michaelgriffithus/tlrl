package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.support.ManageableService;

public interface UserService 
		extends ManageableService<Long, User> {

	public User findByName(String name);
	public User findByEmail(String email);
	public User signUpUser(User user);
}
