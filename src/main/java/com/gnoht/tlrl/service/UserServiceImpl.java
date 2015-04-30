package com.gnoht.tlrl.service;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.UserJpaRepository;

@Service("userService")
public class UserServiceImpl 
			extends ManagedService<Long, User, UserJpaRepository> 
		implements UserService {

	@Resource(name="rememberMeTokenService")
	private PersistentTokenRepository persistentTokenRepository;
	
	@Inject
	public UserServiceImpl(UserJpaRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public User findByName(String name) {
		return getRepository().findOneByName(name);
	}

	@Override
	public User findByEmail(String email) {
		return getRepository().findOneByEmail(email);
	}

	@Transactional(readOnly=false)
	@Override
	public User signUpUser(User unconfirmedUser) {
		User user = getRepository().findOneByEmail(unconfirmedUser.getEmail());
		if(user != null) {
			user.setEnabled(true);
			user.setName(unconfirmedUser.getName());
			user.setRole(new Role("ROLE_USER"));
			persistentTokenRepository.removeUserTokens(unconfirmedUser.getEmail());
			return getRepository().saveAndFlush(user);
		}
		//TODO: exception
		return unconfirmedUser;
	}
}
