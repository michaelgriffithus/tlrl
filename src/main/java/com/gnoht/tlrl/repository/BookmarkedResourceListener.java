package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkedResource;
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.service.ReadLaterWebPageService;

/**
 * A {@link BookmarkedResource} specfic {@link PostInsertEventListener} that fires
 * of indexing of a {@link BookmarkedResource} (and it's {@link Bookmark}).
 */
public class BookmarkedResourceListener extends 
		ManageablePostInsertEventListener<BookmarkedResource> {

	private static final long serialVersionUID = -8285651372419478302L;

	private ReadLaterWebPageService webPageService;
	
	public BookmarkedResourceListener(ReadLaterWebPageService service) {
		this.webPageService = service;
	}

	@Override
	public void handleSuccess(BookmarkedResource resource, PostInsertEvent event) {
		LOG.info("Starting handleSuccess(): resource={}, event={}", resource, event);
		ReadLaterWebPage webPage = new ReadLaterWebPage(resource.getBookmark());
		webPage.setContent(new String(resource.getContent()));
		webPageService.save(webPage);
	}

	@Override
	public void handleFailure(BookmarkedResource resource, PostInsertEvent event) {
		LOG.info("Starting handleFailure(): resource={}, event={}", resource, event);
		ReadLaterWebPage webPage = new ReadLaterWebPage(resource.getBookmark());
		webPageService.save(webPage);
	}
	
	@Override
	Class<BookmarkedResource> getSupportedClass() {
		return BookmarkedResource.class;
	}
}
