package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gnoht.tlrl.domain.BookmarkedResource;

/**
 * Handles persistence of {@link BookmarkedResource} with underlying datastore.
 */
public interface BookmarkedResourceRepository 
		extends JpaRepository<BookmarkedResource, Long> {

}
