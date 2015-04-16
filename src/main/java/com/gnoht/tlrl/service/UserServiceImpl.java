package com.gnoht.tlrl.service;

import javax.inject.Inject;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.UserRepository;
import com.gnoht.tlrl.service.support.ManagedService;

@Service("userService")
public class UserServiceImpl extends ManagedService<Long, User, UserRepository> 
		implements UserService {

	@Inject
	public UserServiceImpl(UserRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public User findByEmail(String email) {
		return null;
	}

}
