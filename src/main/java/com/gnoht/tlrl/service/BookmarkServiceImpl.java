package com.gnoht.tlrl.service;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.Bookmark.ReadLater;
import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;
import com.gnoht.tlrl.repository.BookmarkRepository;
import com.gnoht.tlrl.service.support.ManagedService;

/**
 * {@link BookmarkService} implementation backed by {@link BookmarkRepository}.
 */
@Transactional(readOnly=true)
@Service("bookmarkService")
public class BookmarkServiceImpl 
			extends ManagedService<Long, Bookmark, BookmarkRepository>
		implements BookmarkService {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkServiceImpl.class);
	
	@Resource
	private WebResourceService webResourceService;
	
	@Inject
	public BookmarkServiceImpl(BookmarkRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Transactional(readOnly=false)
	@Override
	public Bookmark create(Bookmark bookmark) {
		LOG.info("Starting create(): bookmark={}", bookmark);
		
		/* Get the referenced WebResource, otherwise create it, 
		 * if first time being bookmarked */
		WebResource webResource = webResourceService
			.findOrCreate(WebResource.builder()
					.url(bookmark.getUrl())
					.user(bookmark.getUser())
					.get());
		
		return super.create(Bookmark
			.updater(bookmark)
				.webResource(webResource)
				.get());
	}
	
	@Transactional(readOnly=false)
	@Override
	public Bookmark update(Long id, Boolean shared)
			throws ManageableNotFoundException {
		
		Bookmark toUpdate = Bookmark
			// use get vs findOne, as latter will not throw NotFoundException	
			.updater(get(id)) 
			.shared(shared)
			.get();
		
		return save(toUpdate);
	}

	@Transactional(readOnly=false)
	@Override
	public Bookmark update(Long id, ReadLater readLater)
				throws ManageableNotFoundException {
		
		Bookmark toUpdate = Bookmark
			// use get vs findOne, as latter will not throw NotFoundException	
			.updater(get(id)) 
			.readLater(readLater)
			.get();
		
		return save(toUpdate);
	}

	@Transactional(readOnly=false)
	@Override
	public Bookmark update(Bookmark bookmark)
			throws ManageableNotFoundException {
		
		// use get vs findOne, as latter will not throw NotFoundException	
		Bookmark toUpdate = Bookmark.updater(get(bookmark.getId()))
			.title(bookmark.getTitle())
			.description(bookmark.getDescription())
			.tags(bookmark.getTags())
			.get();
		
		return save(toUpdate);
	}

	@Override
	public Bookmark findByUrlAndUser(String url, User user) {
		LOG.info("Starting findByUrlAndUser(): url={}, user={}", url, user);
		return repository.findByWebResourceUrlAndUser(url, user);
	}
}
