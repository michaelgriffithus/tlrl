package com.gnoht.tlrl.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.WebResourceNew;
import com.gnoht.tlrl.repository.WebResourceRepository;
import com.gnoht.tlrl.security.SecurityContextFacade;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.support.ManagedService;

//@Service("webResourceService")
public class WebResourceServiceImpl 
//			extends ManagedService<Long, WebResourceNew, WebResourceRepository> 
//		implements WebResourceService 
		{

	protected static final Logger LOG = LoggerFactory.getLogger(WebResourceServiceImpl.class);
	
//	@Inject
//	public WebResourceServiceImpl(WebResourceRepository repository, 
//			MessageSourceAccessor messageSource) {
//		super(repository, messageSource);
//	}
//
//	@Override
//	public WebResourceNew findByUrlOrCreate(String url) {
//		LOG.info("Starting findOrCreate(): url={}", url);
//		WebResourceNew existing = findByUrl(url);
//		if(existing == null) {
//			LOG.debug("No existing webResource found, creating new webResource.");
//			existing = save(WebResourceNew
//				.builder(url, SecurityUtils.getCurrentUser())
//				.build());
//		}
//		return existing;
//	}
//
//	@Override
//	public WebResourceNew findByUrl(String url) {
//		LOG.info("Starting findByUrl(): url={}", url);
//		return repository.findOneByUrl(url);
//	}

}
