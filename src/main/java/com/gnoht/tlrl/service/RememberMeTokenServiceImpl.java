package com.gnoht.tlrl.service;

import java.util.Date;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.RememberMeToken;
import com.gnoht.tlrl.repository.RememberMeTokenRepository;
import com.gnoht.tlrl.service.support.ManagedService;

/**
 * {@link RememberMeTokenService} implementation that also supports Spring 
 * Security's {@link PersistentTokenRepository}.
 */
@Service("rememberMeTokenService")
public class RememberMeTokenServiceImpl 
			extends ManagedService<Long, RememberMeToken, RememberMeTokenRepository> 
		implements RememberMeTokenService {

	private static final Logger LOG = LoggerFactory.getLogger(RememberMeTokenServiceImpl.class);
	
	@Inject
	public RememberMeTokenServiceImpl(RememberMeTokenRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		LOG.info("Starting createToken(): token={}", token);
		repository.save(new RememberMeToken(token));
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String series) {
		LOG.info("Starting getTokenForSeries(): series={}", series);
		RememberMeToken token = repository.findBySeries(series);
		LOG.debug("Found token={}", token);
		return token == null ? null : new PersistentRememberMeToken(
			token.getUserName(), token.getSeries(), token.getValue(), token.getDate());
	}

	@Override
	public void removeUserTokens(String userName) {
		LOG.info("Starting removeUserToken(): userName={}", userName);
		repository.deleteByUserName(userName);
	}

	@Override
	public void updateToken(String series, String value, Date date) {
		if(repository.update(series, value, date) == 0)
			throw new ManageableNotFoundException(series);
	}
	
}
