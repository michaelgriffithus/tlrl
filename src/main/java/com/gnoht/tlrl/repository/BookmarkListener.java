package com.gnoht.tlrl.repository;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.WebUrl;

public class BookmarkListener {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkListener.class);
	
	@PostUpdate @PostPersist
	public void onPostSave(Bookmark bookmark) {
		LOG.info("Starting onPostSave(): bookmark={}", bookmark);
	}
}
