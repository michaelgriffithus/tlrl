package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.gnoht.tlrl.domain.WebUrl;

/**
 * {@link Repository} interface for {@link WebUrl}s.
 */
public interface WebUrlRepository extends 
		JpaRepository<WebUrl, Long>, WebUrlCustomRepository {

	/**
	 * Find a {@link WebUrl} by given url.
	 * 
	 * @param url of webResource to find.
	 * @return {@link WebUrl} or null.
	 */
	public WebUrl findByUrl(String url);
}
