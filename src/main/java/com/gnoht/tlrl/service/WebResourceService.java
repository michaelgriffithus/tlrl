package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.WebResource;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service interface for {@link WebResource}s.
 */
public interface WebResourceService extends ManageableService<Long, WebResource> {

	/**
	 * Find {@link WebResource} with matching url, creating one if it doesn't already 
	 * exists in the repository.
	 * 
	 * @param url
	 * @return found or newly created {@link WebResource}.
	 */
	WebResource findByUrlOrCreate(String url);
	
	/**
	 * Find a {@link WebResource} with matching url.
	 * 
	 * @param url
	 * @return {@link WebResource} or null.
	 */
	WebResource findByUrl(String url);	
}
