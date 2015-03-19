package com.gnoht.tlrl.service;

import static com.gnoht.tlrl.domain.WebResource.*;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.WebResource;
import com.gnoht.tlrl.repository.WebResourceRepository;
import com.gnoht.tlrl.service.support.ManagedService;

/**
 * {@link BookmarkService} implementation backed by {@link WebResourceRepository}.
 */
@Service("webResourceService")
public class WebResourceServiceImpl extends 
			ManagedService<Long, WebResource, WebResourceRepository> 
		implements WebResourceService {

	private static final Logger LOG = LoggerFactory.getLogger(WebResourceServiceImpl.class);
	
	@Inject
	public WebResourceServiceImpl(WebResourceRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public WebResource findOrCreate(WebResource webResource) {
		LOG.info("Starting findOrCreate(): webResource={}", webResource);
		WebResource existing = repository.findOneByUrl(webResource.getUrl());
		if(existing == null) { 
			LOG.debug("No existing webResource found, creating new webResource.");
			existing = save(updater(webResource).get());
		}
		return existing;
	}

	@Override
	public WebResource findByUrl(String url) {
		LOG.info("Starting findByUrl(): url={}", url);
		return repository.findOneByUrl(url);
	}
}
