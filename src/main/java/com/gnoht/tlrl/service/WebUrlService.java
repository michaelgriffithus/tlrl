package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service interface for {@link WebUrl}s.
 */
public interface WebUrlService extends ManageableService<Long, WebUrl> {

	/**
	 * Find {@link WebUrl} with matching url, creating one if it doesn't already 
	 * exists in the repository.
	 * 
	 * @param url
	 * @return found or newly created {@link WebUrl}.
	 */
	WebUrl findByUrlOrCreate(String url);
	
	/**
	 * Find a {@link WebUrl} with matching url.
	 * 
	 * @param url
	 * @return {@link WebUrl} or null.
	 */
	WebUrl findByUrl(String url);	
}
