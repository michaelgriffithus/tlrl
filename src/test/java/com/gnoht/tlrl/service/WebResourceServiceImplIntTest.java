package com.gnoht.tlrl.service;

import static com.gnoht.tlrl.domain.WebResource.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.config.ApplicationConfig;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.config.ServiceConfig;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.domain.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={ApplicationConfig.class, RepositoryConfig.class, ServiceConfig.class})
@ActiveProfiles("test")
public class WebResourceServiceImplIntTest {

	@Autowired
	private WebPageService webResourceService;
	
	@Test
	public void findOrCreateShouldFindExisting() {
		// given
		WebPage existing = webResourceService.findById(1L);
		assertNotNull("Existing webResource not found", existing);
		
		// when
		WebPage toSave = new WebPage(existing.getUrl());
			toSave.setUser(existing.getUser());
		toSave = webResourceService.findOrCreate(toSave);
		
		//then
		assertEquals("Should have existing id!", existing.getId(), toSave.getId());
		assertEquals("Should have same create date!", 
				existing.getDateCreated(), toSave.getDateCreated());
	}
	
	@Test
	public void findOrCreateShouldCreate() {
		// given
		User user = new User();
		user.setId(1L);
		WebPage toSave = new WebPage("http://yahoo.com");
		toSave.setUser(user);
		
		assertNull(toSave.getId());
		
		// when
		WebResource saved = webResourceService.findOrCreate(toSave);
		
		// then
		assertNotNull(saved.getId());
	}
}
