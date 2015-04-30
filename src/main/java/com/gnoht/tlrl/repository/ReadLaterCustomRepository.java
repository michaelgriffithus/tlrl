package com.gnoht.tlrl.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.controller.Filters;
import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.SharedStatus;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;

public interface ReadLaterCustomRepository {

	public List<ReadLater> findAllByUserAndTags(User user, Set<String> tags, Pageable pageable);
	public List<ReadLater> findAllByTags(Set<String> tags, Pageable pageable);
	public ReadLaterStats findReadLaterStatsByUserAndTags(User user, Set<String> tags);
	public ReadLaterStats findReadLaterStatsByTags(Set<String> tags);
	
	public List<ReadLater> findAllByOwnerAndUntagged(User owner, Filters filters, Pageable pageable);
	public List<ReadLater> findAllByOwnerAndTagged(User owner, Filters filters, Set<String> tags, Pageable pageable);
	
	public ReadLaterStats findReadLaterStatsByOwnerAndUntagged(User owner, Filters filters);
	public ReadLaterStats findReadLaterStatsByOwnerAndTagged(User owner, Filters filters, Set<String> tags);
	
	public ReadLaterStats findPopularTags();
	public ReadLaterStats findRecentTags();
	public List<ReadLater> findRecent(Pageable pageable);
	public List<ReadLater> findPopular(Pageable pageable);
	public WebPage findAllByWebPage(Long webPageId);
	
}
