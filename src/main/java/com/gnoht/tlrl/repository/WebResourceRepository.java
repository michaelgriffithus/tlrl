package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.gnoht.tlrl.domain.WebResourceNew;

/**
 * {@link Repository} interface for {@link WebResourceNew}s.
 */
public interface WebResourceRepository 
		extends 
			//JpaRepository<WebResourceNew, Long>, 
			WebResourceCustomRepository {

	/**
	 * Find a {@link WebResourceNew} by given url.
	 * 
	 * @param url of webResource to find.
	 * @return {@link WebResourceNew} or null.
	 */
	WebResourceNew findOneByUrl(String url);
}
