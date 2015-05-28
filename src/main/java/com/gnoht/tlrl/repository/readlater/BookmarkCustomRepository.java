package com.gnoht.tlrl.repository.readlater;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.controller.ReadLaterQueryFilter;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.BookmarkPageRequest;

public interface BookmarkCustomRepository {

	public List<Bookmark> findAllByUserAndTags(User user, Set<String> tags, Pageable pageable);
	public List<Bookmark> findAllByTags(Set<String> tags, Pageable pageable);
	
	public ReadLaterStats findReadLaterStatsByUserAndTags(User user, Set<String> tags);
	public ReadLaterStats findReadLaterStatsByTags(Set<String> tags);
	
	@Deprecated
	public List<Bookmark> findAllByOwnerAndUntagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Pageable pageable);
	@Deprecated
	public List<Bookmark> findAllByOwnerAndTagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags, Pageable pageable);

	public List<Bookmark> findAllByOwnerAndUntagged(User owner, BookmarkPageRequest pageable);
	public List<Bookmark> findAllByOwnerAndTagged(User owner, Set<String> tags, BookmarkPageRequest pageable);
	public ReadLaterStats findAllMetaByOwnerAndUntagged(User owner);
	public ReadLaterStats findAllMetaByOwnerAndTagged(User owner, Set<String> tags);
	
	public ReadLaterStats findReadLaterStatsByOwnerAndUntagged(User owner, ReadLaterQueryFilter readLaterQueryFilter);
	public ReadLaterStats findReadLaterStatsByOwnerAndTagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags);
	
	public ReadLaterStats findPopularTags();
	public ReadLaterStats findRecentTags();
	public List<Bookmark> findRecent(Pageable pageable);
	public List<Bookmark> findPopular(Pageable pageable);
	
	Page<Bookmark> findPopularByWebUrl(Long id);
	
}
