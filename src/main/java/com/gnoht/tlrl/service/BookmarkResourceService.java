package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkResource;

/**
 * Service for managing {@link BookmarkResource}s.
 */
public interface BookmarkResourceService {

	/**
	 * Crawls the url of given {@link Bookmark} and retrieves the resource.
	 * 
	 * @param bookmark Bookmark instance containing the url to crawl
	 * @return BookmarkResource
	 */
	BookmarkResource crawl(Bookmark bookmark);
}
