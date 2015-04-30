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

@Service("webPageService")
public class WebPageServiceImpl extends ManagedService<Long, WebPage, WebPageJpaRepository> 
		implements WebPageService {

	private static final Logger LOG = LoggerFactory.getLogger(WebPageServiceImpl.class);
	
	@Resource private WebResourceFetcher<WebPage> resourceFetcher;

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
			LOG.debug("No webPage found, creating new entry!");
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

//	@Resource private IndexedWebPageService indexedWebPageService;
//	@Resource private WebResourceFetcher<WebPage> webResourceFetcher;
	
//	@Inject()
//	public WebPageServiceImpl(WebPageMongoRepository repository,
//			MessageSourceAccessor messageSourceAccessor) {
//		super(repository, messageSourceAccessor);
//	}
//
//	@Override
//	public Page<WebPage> findAllByUserIdAndTags(
//			String userId, Set<String> tags, boolean isPartial, Pageable pageable) {
//		Page<WebPage> webPages = getRepository().findAllByUserIdAndTags(userId, tags, pageable);
//		List<Tag> webPageTags = getRepository().findAllTagsByUserIdAndTags(userId, tags);
//		return webPages;
//	}
//
//	@Override
//	public WebPage findById(String id, boolean isPartial) {
//		return getRepository().findById(id);
//	}
//
//	@Override
//	public WebPage create(WebPage webPage) {
//		webPage = save(webPage);
//		indexedWebPageService.create(new IndexedWebPage(webPage));
//		webResourceFetcher.fetch(webPage);
//		return webPage;
//	}
//	
//	@Override
//	public WebPage create(String url) {
//		return create(new WebPage(url));
//	}
//
//	@Override
//	public WebPage update(WebPage updatedWebPage)
//			throws ManageableNotFoundException {
//		return update(updatedWebPage, false);
//	}
//
//	@Override
//	public WebPage delete(String id) throws ManageableNotFoundException {
//		WebPage webPage = get(id);
//		repository.delete(webPage);
//		indexedWebPageService.delete(id);
//		return webPage;
//	}
//
//	@Override
//	public List<Tag> findAllTagsByUserIdAndTags(String userId, Set<String> tags) {
//		return repository.findAllTagsByUserIdAndTags(userId, tags);
//	}
//
//	@Override
//	public List<Tag> findAllTagsByUserId(String userId) {
//		return repository.findAllTagsByUserId(userId);
//	}
//
//	@Override
//	public void deleteAll() {
//		throw new UnsupportedOperationException();
//	}
//
//	@Override
//	public WebPage setFetchedContent(WebPage fetched)
//			throws ManageableNotFoundException {
//		WebPage webPage = get(fetched.getId());
//		webPage.setContent(fetched.getContent());
//		// title is either user defined title, fetched title, or url
//		webPage.setTitle(webPage.getTitle() == null ? 
//				(fetched.getTitle() == null ? fetched.getUrl() : fetched.getTitle()) : 
//					webPage.getTitle());
//		indexedWebPageService.setFetchedContent(webPage);
//		return save(webPage);
//	}
//
}
