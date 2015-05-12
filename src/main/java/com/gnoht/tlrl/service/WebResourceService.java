package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.WebResourceNew;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service interface for {@link WebResourceNew}s.
 */
public interface WebResourceService 
		extends ManageableService<Long, WebResourceNew> {

	/**
	 * Find {@link WebResourceNew} with matching url, creating one if it doesn't already 
	 * exists in the repository.
	 * 
	 * @param url
	 * @return found or newly created {@link WebResourceNew}.
	 */
	WebResourceNew findByUrlOrCreate(String url);
	
	/**
	 * Find a {@link WebResourceNew} with matching url.
	 * 
	 * @param url
	 * @return {@link WebResourceNew} or null.
	 */
	WebResourceNew findByUrl(String url);	
}
