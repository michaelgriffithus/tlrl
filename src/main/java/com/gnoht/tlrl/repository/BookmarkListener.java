package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkResource;
import com.gnoht.tlrl.service.BookmarkResourceService;
import com.gnoht.tlrl.service.ReadLaterWebPageService;

/**
 * {@link Bookmark} specific {@link PostInsertEventListener} that firsts off a 
 * crawl for {@link BookmarkResource} whenever a new Bookmark is added.
 */
public class BookmarkListener extends ManageableEventListener<Bookmark> {

	private static final long serialVersionUID = 5956032168972510149L;
	
	private final BookmarkResourceService bookmarkResourceService;
	private final ReadLaterWebPageService readLaterWebPageService;

	public BookmarkListener(BookmarkResourceService bookmarkResourceService, 
			ReadLaterWebPageService readLaterWebPageService) {
		this.bookmarkResourceService = bookmarkResourceService;
		this.readLaterWebPageService = readLaterWebPageService;
	}
	
	@Override
	public void handlePostInsertSuccess(Bookmark bookmark, PostInsertEvent event) {
		LOG.info("Starting handlePostInsertSuccess(): bookmark={}, event={}", bookmark, event);
		bookmarkResourceService.crawl(bookmark);
	}

	@Override
	public void handlePostInsertFailure(Bookmark bookmark, PostInsertEvent event) {
		LOG.info("Starting handlePostInsertFailure(): bookmark={}, event={}", bookmark, event);
		LOG.info("Skipping crawl since we were unable to save Bookmark!");
	}

	@Override
	public void handlePostUpdateSuccess(Bookmark bookmark, PostUpdateEvent event) {
		LOG.info("Starting handlePostUpdateSuccess(): bookmark={}, event={}", bookmark, event);
		readLaterWebPageService.updateFrom(bookmark);
	}

	@Override
	public void handlePostUpdateFailure(Bookmark bookmark, PostUpdateEvent event) {
		LOG.info("Starting handlePostUpdateFailure(): bookmark={}, event={}", bookmark, event);
	}

	@Override
	public void handlePostDeleteSuccess(Bookmark bookmark, PostDeleteEvent event) {
		LOG.info("Starting handlePostDeleteSuccess(): bookmark={}, event={}", bookmark, event);
		readLaterWebPageService.delete(bookmark.getId().toString());
	}

	@Override
	public void handlePostDeleteFailure(Bookmark bookmark, PostDeleteEvent event) {
		LOG.info("Starting handlePostDeleteFailure(): bookmark={}, event={}", bookmark, event);
	}

	@Override
	Class<Bookmark> getSupportedClass() {
		return Bookmark.class;
	}
}
