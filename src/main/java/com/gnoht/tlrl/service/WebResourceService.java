package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.WebResource;
import com.gnoht.tlrl.service.support.ManageableService;

/**
 * Service interface for {@link WebResource}s.
 */
public interface WebResourceService 
		extends ManageableService<Long, WebResource>{

	/**
	 * Find the given {@link WebResource}, creating one if it doesn't already 
	 * exists in the repository.
	 * 
	 * @param webResource to find or create.
	 * @return found or newly created WebResource.
	 */
	WebResource findOrCreate(WebResource webResource);
}
