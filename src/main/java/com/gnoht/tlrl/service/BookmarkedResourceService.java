package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkedResource;

/**
 * Service for managing {@link BookmarkedResource}s.
 */
public interface BookmarkedResourceService {

	/**
	 * Crawls the url of given {@link Bookmark} and retrieves the resource.
	 * 
	 * @param bookmark Bookmark instance containing the url to crawl
	 * @return BookmarkedResource
	 */
	BookmarkedResource crawl(Bookmark bookmark);
}
