package com.gnoht.tlrl.service;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;
//import com.gnoht.tlrl.repository.WebPageMongoRepository;
import com.gnoht.tlrl.repository.WebResourceRepository;
import com.gnoht.tlrl.repository.WebResourceRepository;
import com.gnoht.tlrl.security.SecurityUtils;
//import com.gnoht.tlrl.domain.Tag;
//import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.service.support.ManagedService;

@Service("webPageService")
public class WebResourceServiceImpl extends ManagedService<Long, WebResource, WebResourceRepository> 
		implements WebResourceService {

	protected static final Logger LOG = LoggerFactory.getLogger(WebResourceServiceImpl.class);
	
	@Inject
	public WebResourceServiceImpl(WebResourceRepository repository, 
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public WebResource findByUrlOrCreate(String url) {
		LOG.info("Starting findOrCreate(): url={}", url);
		WebResource webResource = findByUrl(url);
		if(webResource == null) {
			LOG.debug("No existing webResource found, creating new webResource.");
			webResource = new WebResource(SecurityUtils.getCurrentUser(), url);
			webResource = save(webResource);
		}
		return webResource;
	}

	@Override
	public WebResource findByUrl(String url) {
		LOG.info("Starting findByUrl(): url={}", url);
		return repository.findByUrl(url);
	}
}
