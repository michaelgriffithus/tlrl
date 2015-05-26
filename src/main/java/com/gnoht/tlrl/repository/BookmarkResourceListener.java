package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkResource;
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.service.ReadLaterWebPageService;

/**
 * A {@link BookmarkResource} specfic {@link PostInsertEventListener} that fires
 * of indexing of a {@link BookmarkResource} (and it's {@link Bookmark}).
 */
public class BookmarkResourceListener extends 
		ManageableEventListener<BookmarkResource> {

	private static final long serialVersionUID = -8285651372419478302L;

	private ReadLaterWebPageService webPageService;
	
	public BookmarkResourceListener(ReadLaterWebPageService service) {
		this.webPageService = service;
	}

	@Override
	public void handlePostInsertSuccess(BookmarkResource resource, PostInsertEvent event) {
		LOG.info("Starting handleSuccess(): resource={}, event={}", resource, event);
		ReadLaterWebPage webPage = new ReadLaterWebPage(resource.getBookmark());
		webPage.setContent(new String(resource.getContent()));
		webPageService.save(webPage);
	}

	@Override
	public void handlePostInsertFailure(BookmarkResource resource, PostInsertEvent event) {
		LOG.info("Starting handleFailure(): resource={}, event={}", resource, event);
		ReadLaterWebPage webPage = new ReadLaterWebPage(resource.getBookmark());
		webPageService.save(webPage);
	}
	
	@Override
	public void handlePostUpdateSuccess(BookmarkResource entity,
			PostUpdateEvent event) {
	}

	@Override
	public void handlePostUpdateFailure(BookmarkResource entity,
			PostUpdateEvent event) {
	}

	@Override
	public void handlePostDeleteSuccess(BookmarkResource entity,
			PostDeleteEvent event) {
	}

	@Override
	public void handlePostDeleteFailure(BookmarkResource entity,
			PostDeleteEvent event) {
	}

	@Override
	Class<BookmarkResource> getSupportedClass() {
		return BookmarkResource.class;
	}
}
