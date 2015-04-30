package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;

public interface UserService 
		extends ManageableService<Long, User> {

	public User findByName(String name);
	public User findByEmail(String email);
	public User confirmUser(User user);
}
