package com.gnoht.tlrl.repository;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.config.RepositoryConfig;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={RepositoryConfig.class})
@ActiveProfiles("test")
public class BookmarkRepositoryIntTest {

	@Autowired
	private BookmarkRepository bookmarkRepository;
	
	@Test
	public void repositoryShouldNotBeNull() {
		assertNotNull(bookmarkRepository);
	}
	
	@Test
	public void shouldSaveBookmark() {
		WebResource existingWebResource = WebResource
				.builder()
				.id(1L)
				.get();
		
		User user = new User(1L);
		
		// given
		Bookmark bookmark = Bookmark
			.builder(existingWebResource, user)
			.get();
		
		assertNull("Id should be null!", bookmark.getId());
		
		// when
		bookmarkRepository.save(bookmark);
		
		// then
		assertNotNull("Id should not be null", bookmark.getId());
	}
}
