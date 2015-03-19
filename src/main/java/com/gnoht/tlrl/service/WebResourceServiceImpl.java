package com.gnoht.tlrl.service;

import static com.gnoht.tlrl.domain.WebResource.*;

import javax.inject.Inject;

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

	@Inject
	public WebResourceServiceImpl(WebResourceRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public WebResource findOrCreate(WebResource webResource) {
		WebResource existing = repository.findOneByUrl(webResource.getUrl());
		if(existing == null) 
			existing = save(updater(webResource).get());
		return existing;
	}
}
