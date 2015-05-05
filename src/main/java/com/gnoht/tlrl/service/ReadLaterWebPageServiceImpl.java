package com.gnoht.tlrl.service;


import java.util.Set;

import javax.inject.Inject;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.ReadLaterWebPageSolrRepository;
import com.gnoht.tlrl.repository.SearchResultPage;
import com.gnoht.tlrl.service.support.ManagedService;

@Service("readLaterWebPageService")
public class ReadLaterWebPageServiceImpl extends 
				ManagedService<String, ReadLaterWebPage, ReadLaterWebPageSolrRepository> 
		implements ReadLaterWebPageService {


	@Inject
	public ReadLaterWebPageServiceImpl(ReadLaterWebPageSolrRepository repository,
			MessageSourceAccessor messageSourceAccessor) {
		super(repository, messageSourceAccessor);
	}
	
	@Override
	public SearchResultPage search(Set<String> terms, Set<String> tags, Pageable pageable) {
		return getRepository().search(terms, tags, pageable);
	}

	@Override
	public SearchResultPage search(Set<String> terms, Set<String> tags,
			User user, User caller, Pageable pageable) {
		boolean isOwner = (caller != null && user != null && 
				caller.getId().equals(user.getId()));
		return getRepository().search(terms, tags, user, isOwner, pageable);
	}
}
