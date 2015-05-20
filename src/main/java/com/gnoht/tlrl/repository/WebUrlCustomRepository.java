package com.gnoht.tlrl.repository;

import com.gnoht.tlrl.domain.WebUrl;

public interface WebUrlCustomRepository {

	/**
	 * Finds the {@link WebUrl} identified by given id. 
	 * 
	 * @param id
	 * @return
	 */
	WebUrl findOneById(Long id);
}
