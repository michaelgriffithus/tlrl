package com.gnoht.tlrl.repository;

import org.springframework.data.solr.repository.SolrCrudRepository;

import com.gnoht.tlrl.domain.ReadLaterWebPage;

/**
 * Provides {@link SolrCrudRepository} functionality for {@link ReadLaterWebPage}s.
 */
public interface ReadLaterWebPageSolrRepository 
		extends SolrCrudRepository<ReadLaterWebPage, String>, ReadLaterWebPageCustomSolrRepository<ReadLaterWebPage> {
}
