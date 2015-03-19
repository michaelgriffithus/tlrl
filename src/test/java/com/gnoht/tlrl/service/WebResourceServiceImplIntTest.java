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
import com.gnoht.tlrl.domain.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={ApplicationConfig.class, RepositoryConfig.class, ServiceConfig.class})
@ActiveProfiles("test")
public class WebResourceServiceImplIntTest {

	@Autowired
	private WebResourceService webResourceService;
	
	@Test
	public void findOrCreateShouldFindExisting() {
		// given
		WebResource existing = webResourceService.findById(1L);
		assertNotNull("Existing webResource not found", existing);
		
		// when
		WebResource toSave = builder()
			.url(existing.getUrl())
			.user(existing.getUser())
			.get();
		toSave = webResourceService.findOrCreate(toSave);
		
		//then
		assertEquals("Should have existing id!", existing.getId(), toSave.getId());
		assertEquals("Should have same create date!", 
				existing.getDateCreated(), toSave.getDateCreated());
	}
	
	@Test
	public void findOrCreateShouldCreate() {
		// given
		WebResource toSave = builder()
				.url("http://yahoo.com")
				.user(new User(1L))
				.get();
		
		assertNull(toSave.getId());
		
		// when
		WebResource saved = webResourceService.findOrCreate(toSave);
		
		// then
		assertNotNull(saved.getId());
	}
}
