package com.gnoht.tlrl.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.BookmarkPageRequest;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service for managing {@link Bookmark}s.
 */
public interface BookmarkService 
		extends ManageableService<Long, Bookmark> {
	
	/**
	 * Finds {@link Bookmark} matching the given one or create a new Bookmark
	 * with the given one.
	 *  
	 * @param bookmark Bookmark with attributes to find or create
	 * @return Existing or newly created Bookmark.
	 */
	Bookmark findOrCreate(Bookmark bookmark);
	
	public Bookmark updateReadLaterStatus(Bookmark bookmark);

//	public ResultPage<Bookmark> findAllByOwnerAndTagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags, Pageable pageable);
//	public ResultPage<Bookmark> findAllByOwnerAndUntagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Pageable pageable);
	public ResultPage<Bookmark> findAllByOwnerAndTagged(User owner, Set<String> tags, BookmarkPageRequest pageable);
	public ResultPage<Bookmark> findAllByOwnerAndUntagged(User owner, BookmarkPageRequest pageable);
	
	public ResultPage<Bookmark> findAllByUserAndTagged(User user, Set<String> tags, Pageable pageable);
	public ResultPage<Bookmark> findAllTagged(Set<String> tags, Pageable pageable);
	
	public ResultPage<Bookmark> findRecent(Pageable pageable);
	public ResultPage<Bookmark> findPopular(Pageable pageable);
	
	public Page<Bookmark> findPopularByWebUrl(Long id);
	
}
