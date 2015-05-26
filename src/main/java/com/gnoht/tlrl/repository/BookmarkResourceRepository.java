package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gnoht.tlrl.domain.BookmarkResource;

/**
 * Handles persistence of {@link BookmarkResource} with underlying datastore.
 */
public interface BookmarkResourceRepository 
		extends JpaRepository<BookmarkResource, Long> {

}
