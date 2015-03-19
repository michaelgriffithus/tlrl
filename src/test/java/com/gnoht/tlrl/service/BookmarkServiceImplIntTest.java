package com.gnoht.tlrl.service;

import static org.junit.Assert.*;
import static com.gnoht.tlrl.domain.Bookmark.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={
		ApplicationConfig.class, RepositoryConfig.class, ServiceConfig.class})
@ActiveProfiles("test")
public class BookmarkServiceImplIntTest {

	@Resource
	BookmarkService bookmarkService;

	@Resource
	WebResourceService webResourceService;
	
	User testUser = new User(1L);

	/**
	 * Validates adding {@link Bookmark} will not create WebResource if
	 * it WebResource already exists in our system.
	 */
	@Test
	public void shouldOnlyBookmarkAndNotCreateWebResource() {
		List<WebResource> webResources = webResourceService.findAll();
		assertFalse("Should have some existing webResources!", webResources.isEmpty());
		
		WebResource toBookmark = webResources.get(0);
		assertNull("Bookmark already exists!", bookmarkService.findByUrlAndUser(toBookmark.getUrl(), testUser));
		
		bookmarkService.create(
			builder(toBookmark.getUrl(), testUser)
				.get());
		
		// check to see if webResource count changed after adding bookmark
		assertTrue("Another webResource was created!", webResourceService.count() == webResources.size());
		assertNotNull("Bookmark was not created!", bookmarkService.findByUrlAndUser(toBookmark.getUrl(), testUser));
	}

	/**
	 * Validates adding new {@link Bookmark} will create WebResource if
	 * it doesn't exists in our system.
	 */
	@Test
	public void shouldBookmarkAndCreateWebResource() {
		String randomUrl = "http://domain.com/" + System.currentTimeMillis(); 
		assertNull(webResourceService.findByUrl(randomUrl));
		
		bookmarkService.create(
			builder(randomUrl, testUser)
				.get());
		
		assertNotNull(webResourceService.findByUrl(randomUrl));
	}
	
}
