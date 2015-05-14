package com.gnoht.tlrl.service;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;
import com.gnoht.tlrl.security.OAuth2Authentication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class WebResourceServiceImplIntTest {

	@Autowired WebResourceService webResourceService;
	@Autowired UserService userService;
	
	@Before
	public void setUp() {
		User user = userService.findByName("thong");
		SecurityContextHolder.getContext().setAuthentication(new OAuth2Authentication(user));
	}
	
	@Test
	public void findByUrlOrCreateShouldFindExisting() {
		// given
		WebResource existing = webResourceService.findByUrl("http://losangeles.craigslist.org/");
		assertNotNull("Existing webResource not found", existing);
		
		// when
		WebResource newBookmark = webResourceService
			.findByUrlOrCreate(existing.getUrl());
		
		//then
		assertEquals("Should have existing id!", existing.getId(), newBookmark.getId());
		assertEquals("Should have same create date!", 
				existing.getDateCreated(), newBookmark.getDateCreated());
	}
	
	@Test
	public void findByUrlOrCreateShouldCreate() {
		// given
		String urlToCreate = "http://yahoo.com";
		assertNull(webResourceService.findByUrl(urlToCreate));
		
		// when
		WebResource saved = webResourceService.findByUrlOrCreate(urlToCreate);
		
		// then
		assertNotNull(webResourceService.findByUrl(urlToCreate));
		assertNotNull(saved.getId());
	}
}
