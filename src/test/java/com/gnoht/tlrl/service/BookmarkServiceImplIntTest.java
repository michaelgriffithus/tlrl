package com.gnoht.tlrl.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;
import com.gnoht.tlrl.security.OAuth2Authentication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@IntegrationTest
@ActiveProfiles("test")
public class BookmarkServiceImplIntTest {

	@Resource BookmarkService bookmarkService;
	@Resource BookmarkRepository bookmarkRepository;
	@Resource UserService userService;
	
	@Mock BookmarkResourceService bookmarkResourceService;

	
	Bookmark bookmark;
	User user;
	
	@Before
	public void setUp() {
		user = userService.findByName("thong");
		bookmark = new Bookmark("http://craigslist.org");
		SecurityContextHolder.getContext().setAuthentication(new OAuth2Authentication(user));
	}
	
	@Transactional
	//@Test
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
	//@Test
	public void findOrCreateShouldReturnExistingBookmark() {
		bookmarkService.create(bookmark);
		assertNotNull(bookmarkRepository.findOneByUserAndWebUrlUrl(user, bookmark.getUrl()));
		
		Bookmark duplicateBookmark = new Bookmark(bookmark.getUrl());
		Bookmark created = bookmarkService.findOrCreate(duplicateBookmark);
		
		assertTrue(created.equals(bookmark));
		assertTrue(created.getUser().getId().equals(user.getId()));
	}

	@Transactional
	@Rollback(false)
	@Test
	public void shouldUpdateExistingBookmark() {
		bookmarkService.create(bookmark);
		Bookmark toUpdate = bookmarkService.findById(bookmark.getId());
		toUpdate.setTitle("hello");
		System.out.println("about to update ===================== ");
		bookmarkService.save(toUpdate);
	}
	
}
