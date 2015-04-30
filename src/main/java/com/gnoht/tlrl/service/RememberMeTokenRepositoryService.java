package com.gnoht.tlrl.service;

import java.util.Date;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.repository.RememberMeTokenJpaRepository;
import com.gnoht.tlrl.security.RememberMeToken;
import com.gnoht.tlrl.security.RememberMeTokenService;

@Service(value="rememberMeTokenService")
public class RememberMeTokenRepositoryService 
			extends ManagedService<Long, RememberMeToken, RememberMeTokenJpaRepository> 
		implements PersistentTokenRepository, RememberMeTokenService {

	private static final Logger LOG = LoggerFactory.getLogger(RememberMeTokenRepositoryService.class);
	
	@Inject
	public RememberMeTokenRepositoryService(
			RememberMeTokenJpaRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		getRepository().save(new RememberMeToken(token));
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String series) {
		RememberMeToken token = getRepository().findBySeries(series);
		return token == null ? null : new PersistentRememberMeToken(
				token.getUserName(), token.getSeries(), token.getValue(), token.getDate());
	}

	@Override
	public void removeUserTokens(String userName) {
		getRepository().deleteByUserName(userName);
	}

	@Override
	public void updateToken(String series, String value, Date date) {
		int updateCount = getRepository().update(series, value, date);
		if(updateCount == 0) {
			throw new ManageableNotFoundException();
		}
	}

}
