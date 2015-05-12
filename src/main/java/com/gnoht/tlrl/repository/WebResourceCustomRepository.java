package com.gnoht.tlrl.repository;

import com.gnoht.tlrl.domain.WebResourceNew;

public interface WebResourceCustomRepository {

	/**
	 * Finds the {@link WebResourceNew} identified by given id. 
	 * 
	 * @param id
	 * @return
	 */
	WebResourceNew findOneById(Long id);
}
