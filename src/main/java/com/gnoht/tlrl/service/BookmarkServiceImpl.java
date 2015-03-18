package com.gnoht.tlrl.service;

import javax.inject.Inject;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.repository.BookmarkRepository;
import com.gnoht.tlrl.service.support.ManagedService;

/**
 * {@link BookmarkService} implementation backed by {@link BookmarkRepository}.
 */
@Service("bookmarkService")
public class BookmarkServiceImpl 
			extends ManagedService<Long, Bookmark, BookmarkRepository>
		implements BookmarkService {

	@Inject
	public BookmarkServiceImpl(BookmarkRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}
}
