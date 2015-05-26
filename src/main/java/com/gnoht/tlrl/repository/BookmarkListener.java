package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkResource;
import com.gnoht.tlrl.service.BookmarkResourceService;

/**
 * {@link Bookmark} specific {@link PostInsertEventListener} that firsts off a 
 * crawl for {@link BookmarkResource} whenever a new Bookmark is added.
 */
public class BookmarkListener extends ManageablePostInsertEventListener<Bookmark> {

	private static final long serialVersionUID = 5956032168972510149L;
	
	private final BookmarkResourceService bookmarkResourceService;

	public BookmarkListener(BookmarkResourceService bookmarkResourceService) {
		this.bookmarkResourceService = bookmarkResourceService;
	}
	
	@Override
	public void handleSuccess(Bookmark bookmark, PostInsertEvent event) {
		LOG.info("Starting handleSuccess(): bookmark={}, event={}", bookmark, event);
		bookmarkResourceService.crawl(bookmark);
	}

	@Override
	public void handleFailure(Bookmark bookmark, PostInsertEvent event) {
		LOG.info("Starting handleFailure(): bookmark={}, event={}", bookmark, event);
		LOG.info("Skipping crawl since we were unable to save Bookmark!");
	}

	@Override
	Class<Bookmark> getSupportedClass() {
		return Bookmark.class;
	}
}
