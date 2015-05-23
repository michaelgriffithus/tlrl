package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkedResource;
import com.gnoht.tlrl.service.BookmarkedResourceService;

/**
 * {@link Bookmark} specific {@link PostInsertEventListener} that firsts off a 
 * crawl for {@link BookmarkedResource} whenever a new Bookmark is added.
 */
public class BookmarkListener extends ManageablePostInsertEventListener<Bookmark> {

	private static final long serialVersionUID = 5956032168972510149L;
	
	private final BookmarkedResourceService bookmarkedResourceService;

	public BookmarkListener(BookmarkedResourceService bookmarkedResourceService) {
		this.bookmarkedResourceService = bookmarkedResourceService;
	}
	
	@Override
	public void handleSuccess(Bookmark bookmark, PostInsertEvent event) {
		LOG.info("Starting handleSuccess(): bookmark={}, event={}", bookmark, event);
		bookmarkedResourceService.crawl(bookmark);
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
