package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service interface for {@link Bookmark}s. 
 */
public interface BookmarkService 
		extends ManageableService<Long, Bookmark> {

	/**
	 * Finds a Bookmark by url and {@link User}.
	 * 
	 * @param url
	 * @param user
	 * @return
	 */
	Bookmark findByUrlAndUser(String url, User user);
}
