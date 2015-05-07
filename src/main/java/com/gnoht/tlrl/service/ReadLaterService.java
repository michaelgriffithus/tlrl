package com.gnoht.tlrl.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.controller.ReadLaterQueryFilter;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.repository.ResultPage;

public interface ReadLaterService {

	public Bookmark findOrCreateReadLater(User user, String url);
	
	public Bookmark findOrCreateReadLater(Bookmark bookmark);
	
	public Bookmark findReadLaterById(Long id);
	
	public Bookmark updateReadLater(Bookmark bookmark);
	public Bookmark updateReadLaterStatus(Bookmark bookmark);

	public ResultPage<Bookmark> findAllByOwnerAndTagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags, Pageable pageable);
	public ResultPage<Bookmark> findAllByOwnerAndUntagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Pageable pageable);
	
	public ResultPage<Bookmark> findAllByUserAndTagged(User user, Set<String> tags, Pageable pageable);
	public ResultPage<Bookmark> findAllTagged(Set<String> tags, Pageable pageable);
	
	public Bookmark deleteReadLater(Bookmark bookmark);
	
	public ResultPage<Bookmark> findRecent(Pageable pageable);
	public ResultPage<Bookmark> findPopular(Pageable pageable);
	
	public WebPage findAllByWebPage(Long webPageId);
}
