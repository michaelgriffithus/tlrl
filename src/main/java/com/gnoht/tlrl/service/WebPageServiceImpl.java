package com.gnoht.tlrl.service;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
//import com.gnoht.tlrl.repository.WebPageMongoRepository;
import com.gnoht.tlrl.repository.WebPageJpaRepository;
//import com.gnoht.tlrl.domain.Tag;
//import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.service.support.ManagedService;

@Service("webPageService")
public class WebPageServiceImpl extends ManagedService<Long, WebPage, WebPageJpaRepository> 
		implements WebPageService {

	private static final Logger LOG = LoggerFactory.getLogger(WebPageServiceImpl.class);
	
	@Resource private WebResourceFetcher resourceFetcher;

	@Inject
	public WebPageServiceImpl(WebPageJpaRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public WebPage findOrCreate(WebPage webPage) {
		LOG.debug("Starting findOrCreate(webPage={})", webPage);
		WebPage found = getRepository().findByUrl(webPage.getUrl());
		if(found == null) {
			found = save(webPage);
			LOG.debug("No webPage found, creating new entry!");
			// calls async
			found = resourceFetcher.fetch(webPage);
		}
		LOG.debug("Leaving findOrCreate(): webPage={}", found);
		return found;
	}

	@Override
	public WebPage findOrCreate(User user, String url) {
		LOG.debug("Starting findOrCreate(url={})", url);
		return findOrCreate(new WebPage(user, url));
	}

	@Override
	public WebPage findByUrl(String url) {
		return getRepository().findByUrl(url);
	}
	
	@Override
	public WebPage update(WebPage updated)
			throws ManageableNotFoundException {
		WebPage webPage = findById(updated.getId());
		if(webPage == null)
			throw new ManageableNotFoundException(updated.getId());
		webPage.update(updated);
		webPage = save(webPage);
		return webPage;
	}
}
