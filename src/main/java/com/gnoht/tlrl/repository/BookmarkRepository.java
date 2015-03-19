package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;

/**
 * {@link Repository} interface for {@link Bookmark}s. 
 */
public interface BookmarkRepository 
		extends JpaRepository<Bookmark, Long> {
	
	/**
	 * Finds a Bookmark by url and user. Should only return 1 result
	 * as bookmarks are unique to url+user.
	 * 
	 * @param url 
	 * @param user
	 * @return
	 */
	Bookmark findByWebResourceUrlAndUser(String url, User user);
}
