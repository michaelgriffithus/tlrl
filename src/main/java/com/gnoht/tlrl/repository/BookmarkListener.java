package com.gnoht.tlrl.repository;

import javax.inject.Inject;

import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.service.BookmarkedResourceService;

/**
 * 
 */
public class BookmarkListener implements PostCommitInsertEventListener, PostInsertEventListener {

	private static final long serialVersionUID = 1307765396525536360L;

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkListener.class);
	
	private final BookmarkedResourceService bookmarkedResourceService;

	public BookmarkListener(BookmarkedResourceService bookmarkedResourceService) {
		this.bookmarkedResourceService = bookmarkedResourceService;
	}
	
	@Override
	public void onPostInsert(PostInsertEvent event) {
		LOG.info("onPostInsert(): event.getEntity={}", event.getEntity());
		Bookmark bookmark = (Bookmark) event.getEntity();
		bookmarkedResourceService.crawl(bookmark);
	}

	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		boolean requiresHandling = persister.getEntityName().equals(Bookmark.class.getName()); 
		LOG.info("requiresPostCommitHandling: {}", requiresHandling);
		return requiresHandling;
	}

	@Override
	public void onPostInsertCommitFailed(PostInsertEvent event) {
		LOG.info("Skipping crawl, bookmark save failed!: {}", event.getEntity());
	}
}
