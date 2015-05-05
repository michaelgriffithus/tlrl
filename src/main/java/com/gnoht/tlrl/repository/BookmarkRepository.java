package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.gnoht.tlrl.domain.Bookmark;

/**
 * Provides {@link Repository} support for {@link Bookmark}s.
 */
public interface BookmarkRepository 
		extends JpaRepository<Bookmark, Long>{
}