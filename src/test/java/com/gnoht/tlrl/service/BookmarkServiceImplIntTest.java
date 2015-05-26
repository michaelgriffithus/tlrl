package com.gnoht.tlrl.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;
import com.gnoht.tlrl.security.OAuth2Authentication;
import com.gnoht.tlrl.security.SecurityUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class BookmarkServiceImplIntTest {

	@Resource BookmarkService bookmarkService;
	@Resource BookmarkRepository bookmarkRepository;
	@Resource UserService userService;

	
	Bookmark bookmark;
	User user;
	
	@Before
	public void setUp() {
		user = userService.findByName("thong");
		bookmark = new Bookmark("http://craigslist.org");
		SecurityContextHolder.getContext().setAuthentication(new OAuth2Authentication(user));
	}
	
	@Transactional
	@Test
	public void findOrCreateShouldCreateNewBookmark() {
		assertNull(bookmark.getId());
		assertNull("Bookmark already exists!", bookmarkRepository
				.findOneByUserAndWebUrlUrl(user, bookmark.getUrl()));
		
		Bookmark created = bookmarkService.findOrCreate(bookmark);

		assertNotNull("Bookmark was not created!", bookmarkRepository
				.findOneByUserAndWebUrlUrl(user, bookmark.getUrl()));
		assertTrue(created.getUser().getId().equals(user.getId()));
	}

	@Transactional
	@Test
	public void findOrCreateShouldReturnExistingBookmark() {
		bookmarkService.create(bookmark);
		assertNotNull(bookmarkRepository.findOneByUserAndWebUrlUrl(user, bookmark.getUrl()));
		
		Bookmark duplicateBookmark = new Bookmark(bookmark.getUrl());
		Bookmark created = bookmarkService.findOrCreate(duplicateBookmark);
		
		assertTrue(created.equals(bookmark));
		assertTrue(created.getUser().getId().equals(user.getId()));
	}

	
}
