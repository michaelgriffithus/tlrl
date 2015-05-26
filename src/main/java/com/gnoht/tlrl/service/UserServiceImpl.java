package com.gnoht.tlrl.service;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.AlreadySignedUpException;
import com.gnoht.tlrl.domain.Role;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.UserJpaRepository;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.support.ManagedService;

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
	public User signUpUser(User user) {
		if(getRepository().findOneByEmail(user.getEmail()) != null)
			throw new AlreadySignedUpException("Your email has already been signed up!");
		
		user.setEnabled(true);
		user.setRole(SecurityUtils.ROLE_USER);
		return getRepository().saveAndFlush(user);
	}
	
}
