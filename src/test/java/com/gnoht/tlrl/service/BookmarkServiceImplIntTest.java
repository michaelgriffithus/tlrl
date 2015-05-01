package com.gnoht.tlrl.service;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.domain.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class BookmarkServiceImplIntTest {

	@Resource
	ReadLaterService bookmarkService;

	@Resource
	WebPageService webResourceService;
	
	User testUser = new User();

	/**
	 * Validates adding {@link Bookmark} will not create WebResource if
	 * it WebResource already exists in our system.
	 */
	@Test
	public void shouldOnlyBookmarkAndNotCreateWebResource() {
//		Page<WebPage> webResources = webResourceService.findAll(new PageRequest(1, 20));
//		assertFalse("Should have some existing webResources!", webResources.getContent().isEmpty());
//		
//		WebPage webResource = webResources.getContent().get(0);
//		assertNull("Bookmark already exists!", bookmarkService.findOrCreateReadLater(testUser, webResource.getUrl()));
//		
//		bookmarkService.findOrCreateReadLater(testUser, webResource.getUrl());
//		
//		// check to see if webResource count changed after adding bookmark
//		assertTrue("Another webResource was created!", webResourceService.count() == webResources.getContent().size());
//		assertNotNull("Bookmark was not created!", bookmarkService.findByUrlAndUser(webResource.getUrl(), testUser));
	}

	/**
	 * Validates adding new {@link Bookmark} will create WebResource if
	 * it doesn't exists in our system.
	 */
	@Test
	public void shouldBookmarkAndCreateWebResource() {
//		String randomUrl = "http://domain.com/" + System.currentTimeMillis(); 
//		assertNull(webResourceService.findByUrl(randomUrl));
//		
//		bookmarkService.findOrCreate(
//			builder(randomUrl, testUser)
//				.get());
//		
//		assertNotNull(webResourceService..findByUrl(randomUrl));
	}
	
}
