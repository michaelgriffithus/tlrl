package com.gnoht.tlrl.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class BookmarkRepositoryIntTest {

	@Autowired
	Environment env;
	
	@Autowired
	private BookmarkRepository readLaterRepository;
	
	@Test
	public void repositoryShouldNotBeNull() {
		assertNotNull(readLaterRepository);
	}
	
	@Test
	public void shouldSaveBookmark() {
		WebUrl webUrl = new WebUrl("http://yahoo.com");
		webUrl.setId(1L);
		
		User user = new User();
		user.setId(1L);
		
		// given
		Bookmark bookmark = new Bookmark();
		bookmark.setWebPage(webUrl);
		bookmark.setUser(user);
		
		assertNull("Id should be null!", bookmark.getId());
		
		// when
		readLaterRepository.save(bookmark);
		
		// then
		assertNotNull("Id should not be null", bookmark.getId());
	}
}
