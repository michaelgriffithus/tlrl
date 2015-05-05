package com.gnoht.tlrl.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.domain.WebResource;
import com.gnoht.tlrl.repository.readlater.ReadLaterJpaRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={RepositoryConfig.class})
@ActiveProfiles("test")
public class BookmarkRepositoryIntTest {

	@Autowired
	Environment env;
	
	@Autowired
	private ReadLaterJpaRepository readLaterRepository;
	
	@Test
	public void repositoryShouldNotBeNull() {
		assertNotNull(readLaterRepository);
	}
	
	@Test
	public void shouldSaveBookmark() {
		WebPage webPage = new WebPage("http://yahoo.com");
		webPage.setId(1L);
		
		User user = new User();
		user.setId(1L);
		
		// given
		ReadLater bookmark = new ReadLater();
		bookmark.setWebPage(webPage);
		bookmark.setUser(user);
		
		assertNull("Id should be null!", bookmark.getId());
		
		// when
		readLaterRepository.save(bookmark);
		
		// then
		assertNotNull("Id should not be null", bookmark.getId());
	}
}
