package com.gnoht.tlrl.repository;

import java.util.Set;

import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;

public interface ReadLaterWebPageCustomSolrRepository<T extends ReadLaterWebPage> {

	/**
	 * Search for {@link ReadLaterWebPage}s with given set of terms.
	 * 
	 * @param terms Set of terms to search by.
	 * @param tags Set of tags to filter by.
	 * @param user User id to filter by.
	 * @param pageable
	 * @return {@link ResultPage} containing results of search.
	 */
	public SearchResultPage search(Set<String> terms, Set<String> tags, User user, boolean isOwner, Pageable pageable);
	
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
