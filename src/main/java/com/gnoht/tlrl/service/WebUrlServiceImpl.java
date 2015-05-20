package com.gnoht.tlrl.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.WebUrlRepository;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.support.ManagedService;

@Service("webUrlService")
public class WebUrlServiceImpl extends ManagedService<Long, WebUrl, WebUrlRepository> 
		implements WebUrlService {

	protected static final Logger LOG = LoggerFactory.getLogger(WebUrlServiceImpl.class);
	
	@Inject
	public WebUrlServiceImpl(WebUrlRepository repository, 
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public WebUrl findByUrlOrCreate(String url) {
		LOG.info("Starting findOrCreate(): url={}", url);
		WebUrl webUrl = findByUrl(url);
		if(webUrl == null) {
			LOG.debug("No existing webUrl found, creating new webUrl.");
			webUrl = new WebUrl(SecurityUtils.getCurrentUser(), url);
			webUrl = save(webUrl);
		}
		return webUrl;
	}

	@Override
	public WebUrl findByUrl(String url) {
		LOG.info("Starting findByUrl(): url={}", url);
		return repository.findByUrl(url);
	}
}
