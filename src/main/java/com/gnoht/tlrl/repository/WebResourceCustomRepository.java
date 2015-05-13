package com.gnoht.tlrl.repository;

import com.gnoht.tlrl.domain.WebResource;

public interface WebResourceCustomRepository {

	/**
	 * Finds the {@link WebResourceNew} identified by given id. 
	 * 
	 * @param id
	 * @return
	 */
	WebResource findOneById(Long id);
}
