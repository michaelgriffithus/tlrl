package com.gnoht.tlrl.repository.readlater;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.scheduling.annotation.Async;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;

/**
 * {@link Repository} for managing {@link Bookmark} persistence.
 */
public interface BookmarkRepository extends 
			JpaRepository<Bookmark, Long>, BookmarkCustomRepository {

	/**
	 * Finds a {@link Bookmark} by {@link User} and {@link WebUrl}.
	 * 
	 * @param user The user that owns the bookmark.
	 * @param url The webUrl the bookmark references.
	 * @return Bookmark matching the given user and url filter or null.
	 */
	Bookmark findOneByUserAndWebUrlUrl(User user, String url);
	
	/**
	 * Finds a {@link Bookmark} by {@link User} and it's id.
	 * 
	 * @param user The user that owns the bookmark.
	 * @param id The id of the bookmark.
	 * @return Bookmark matching the given user and id filter or null.
	 */
	Bookmark findOneByUserAndId(User user, Long id);
	
	@Async
	public Future<List<Bookmark>> findByTitle(String title);
	
}
