package com.gnoht.tlrl.service;

import javax.inject.Inject;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.AlreadySignedUpException;
import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.UserRepository;
import com.gnoht.tlrl.security.SecurityUtils;
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
		return repository.findOneByEmail(email);
	}

	@Override
	public User findByName(String name) {
		return repository.findOneByName(name);
	}

	@Override
	public User signUpUser(User user) {
		if(repository.findOneByEmail(user.getEmail()) != null)
			throw new AlreadySignedUpException("Your email has already been signed up!");
		
		user.setEnabled(true);
		user.setRole(SecurityUtils.ROLE_USER);
		return repository.saveAndFlush(user);
	}
}
