package com.gnoht.tlrl.service;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.repository.SearchResultPage;
import com.gnoht.tlrl.service.support.ManageableService;


public interface ReadLaterWebPageService extends ManageableService<String, ReadLaterWebPage> {

	/**
	 * Search for {@link ReadLaterWebPage}s with given set of terms.
	 * 
	 * @param terms Set of terms to search by.
	 * @param tags Set of tags to filter by.
	 * @param user User id to filter by.
	 * @param pageable
	 * @return {@link ResultPage} containing results of search.
	 */
	public SearchResultPage search(Set<String> terms, Set<String> tags, User user, User caller, Pageable pageable);
	
	/**
	 * Search for {@link ReadLaterWebPage}s with given set of terms.
	 * 
	 * @param terms Set of terms to search by.
	 * @param tags Set of tags to filter by.
	 * @param pageable
	 * @return {@link ResultPage} containing results of search.
	 */
	public SearchResultPage search(Set<String> terms, Set<String> tags, Pageable pageable);
}
