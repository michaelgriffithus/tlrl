package com.gnoht.tlrl.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.controller.Filters;
import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.SharedStatus;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.repository.ResultPage;

public interface ReadLaterService {

	public ReadLater findOrCreateReadLater(User user, String url);
	
	public ReadLater findOrCreateReadLater(ReadLater readLater);
	
	public ReadLater findReadLaterById(Long id);
	
	public ReadLater updateReadLater(ReadLater readLater);
	public ReadLater updateReadLaterStatus(ReadLater readLater);

	public ResultPage<ReadLater> findAllByOwnerAndTagged(User owner, Filters filters, Set<String> tags, Pageable pageable);
	public ResultPage<ReadLater> findAllByOwnerAndUntagged(User owner, Filters filters, Pageable pageable);
	
	public ResultPage<ReadLater> findAllByUserAndTagged(User user, Set<String> tags, Pageable pageable);
	public ResultPage<ReadLater> findAllTagged(Set<String> tags, Pageable pageable);
	
	public ReadLater deleteReadLater(ReadLater readLater);
	
	public ResultPage<ReadLater> findRecent(Pageable pageable);
	public ResultPage<ReadLater> findPopular(Pageable pageable);
	
	public WebPage findAllByWebPage(Long webPageId);
}
