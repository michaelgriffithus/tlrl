package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.Bookmark.ReadLater;
import com.gnoht.tlrl.domain.ManageableNotFoundException;
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
	
	/**
	 * Creates the passed in {@link Bookmark} if it doesn't already exists, and 
	 * return the newly created Bookmark or currently existing one.
	 * 
	 * @param bookmark
	 * @return
	 */
	Bookmark findOrCreate(Bookmark bookmark);
	
	/**
	 * Updates a {@link Bookmark}'s {@link SharedStatus} to the passed in 
	 * {@link SharedStatus} value.
	 * 
	 * @param id of Bookmark to update.
	 * @param sharedStatus value to update to.
	 * @return Bookmark that was updated, along with current shared value after update.
	 * @throws ManageableNotFoundException
	 */
	Bookmark update(Long id, Boolean shared) throws ManageableNotFoundException;
	
	/**
	 * Updates a {@link Bookmark}'s {@link ReadLater} state to the passed
	 * in ReadLater state. 
	 * 
	 * @param id of Bookmark to update.
	 * @param readLater state to update to.
	 * @return Bookmark that was updated, along with current state after update.
	 * @throws ManageableNotFoundException
	 */
	Bookmark update(Long id, ReadLater readLater) throws ManageableNotFoundException;
	
}
