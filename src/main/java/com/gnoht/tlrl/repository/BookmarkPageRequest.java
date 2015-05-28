package com.gnoht.tlrl.repository;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * {@link Pageable} implementation with support for Bookmark specific sort
 * properties.
 */
public class BookmarkPageRequest extends PageRequest {

	private static final long serialVersionUID = 1L;
	
	private boolean unreadSortProperty,
		privateSortProperty,
		publicSortProperty,
		untaggedSortProperty;
	
	public BookmarkPageRequest(int page, int size) {
		super(page, size);
	}
	
	public BookmarkPageRequest(Pageable pageable) {
		super(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
		
		Sort sort = getSort();
		if(sort != null) {
			Iterator<Sort.Order> sortIter = getSort().iterator();
			while(sortIter.hasNext()) {
				String property = sortIter.next().getProperty();
				if(property.equals("unread"))
					unreadSortProperty = true;
				else if(property.equals("private"))
					privateSortProperty = true;
				else if(property.equals("public"))
					publicSortProperty = true;
				else if(property.equals("untagged"))
					untaggedSortProperty = true;
			}
		}
	}

	public boolean hasUnreadSortProperty() {
		return unreadSortProperty;
	}
	
	public boolean hasPrivateSortProperty() {
		return privateSortProperty;
	}
	
	public boolean hasPublicSortProperty() {
		return publicSortProperty;
	}
	
	public boolean hasUntaggedSortProperty() {
		return untaggedSortProperty;
	}
}
