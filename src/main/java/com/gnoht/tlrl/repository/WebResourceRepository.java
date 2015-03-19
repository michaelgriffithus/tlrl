package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.gnoht.tlrl.domain.WebResource;

/**
 * {@link Repository} interface for {@link WebResource}s.
 */
public interface WebResourceRepository 
		extends JpaRepository<WebResource, Long>{

	/**
	 * Find a {@link WebResource} by given url.
	 * 
	 * @param url of webResource to find.
	 * @return WebResource or null.
	 */
	WebResource findOneByUrl(String url);
}
