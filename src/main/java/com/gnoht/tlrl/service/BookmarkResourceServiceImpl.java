package com.gnoht.tlrl.service;

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkResource;
import com.gnoht.tlrl.repository.BookmarkResourceRepository;
import com.gnoht.tlrl.service.support.ManagedService;

@Service
public class BookmarkResourceServiceImpl 
			extends ManagedService<Long, BookmarkResource, BookmarkResourceRepository> 
		implements BookmarkResourceService {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkResourceServiceImpl.class); 
	
	@Resource
	private BookmarkService bookmarkService;
	
	@Inject
	public BookmarkResourceServiceImpl(BookmarkResourceRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Transactional
	@Async
	@Override
	public BookmarkResource crawl(Bookmark bookmark) {
		try {
			Response response = Jsoup.connect(bookmark.getUrl())
					.method(Method.GET)
					.timeout(10000) //10secs
					.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36")
					.maxBodySize(1500000) // 1.5MB ~ average page size in 2014
				.execute();
			
			BookmarkResource resource = new BookmarkResource();
			resource.setBookmark(bookmark);
			Document document = response.parse();
			resource.setContent(Jsoup.clean(document.body().text(), Whitelist.basic()));
			resource = save(resource);
			return resource;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
