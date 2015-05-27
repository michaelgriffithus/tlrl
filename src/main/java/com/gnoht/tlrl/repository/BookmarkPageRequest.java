package com.gnoht.tlrl.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * {@link Pageable} implementation with support for Bookmark specific sort
 * properties.
 */
public class BookmarkPageRequest extends PageRequest {

	private static final long serialVersionUID = 6547418522737048133L;
	
	private boolean sortUnread, 
		sortPrivate,
		sortPublic,
		sortUntagged;
	
	public BookmarkPageRequest(int page, String ... properties) {
		super(page, 50);
		
		for(String prop: properties) {
			if(prop.equals("unread"))
				sortUnread = true;
			else if(prop.equals("private"))
				sortPrivate = true;
			else if(prop.equals("public"))
				sortPublic = true;
			else if(prop.equals("untagged"))
				sortUntagged = true;
		}
	}

	public boolean hasSortUnread() {
		return sortUnread;
	}
	
	public boolean hasSortUntagged() {
		return sortUntagged;
	}
	
	public boolean hasSortPublic() {
		return sortPublic;
	}
	
	public boolean hasSortPrivate() {
		return sortPrivate;
	}
}
