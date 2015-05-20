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
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.security.OAuth2Authentication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class WebResourceServiceImplIntTest {

	@Autowired WebUrlService webUrlService;
	@Autowired UserService userService;
	
	@Before
	public void setUp() {
		User user = userService.findByName("thong");
		SecurityContextHolder.getContext().setAuthentication(new OAuth2Authentication(user));
	}
	
	@Test
	public void findByUrlOrCreateShouldFindExisting() {
		// given
		WebUrl existing = webUrlService.findByUrl("http://losangeles.craigslist.org/");
		assertNotNull("Existing webResource not found", existing);
		
		// when
		WebUrl newBookmark = webUrlService
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
		assertNull(webUrlService.findByUrl(urlToCreate));
		
		// when
		WebUrl saved = webUrlService.findByUrlOrCreate(urlToCreate);
		
		// then
		assertNotNull(webUrlService.findByUrl(urlToCreate));
		assertNotNull(saved.getId());
	}
}
