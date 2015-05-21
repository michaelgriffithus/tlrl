package com.gnoht.tlrl.repository;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkedResource;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;
import com.gnoht.tlrl.security.OAuth2Authentication;
import com.gnoht.tlrl.service.BookmarkService;
import com.gnoht.tlrl.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@ActiveProfiles("test")
public class BookmarkedResourceRepositoryIntTest {

	@Autowired BookmarkedResourceRepository bookmarkedResourceRepository;
	@Autowired BookmarkService bookmarkService;
	@Autowired UserService userService;
	
	@Test
	public void testSave() {
		User user = userService.findByName("thong");
		SecurityContextHolder.getContext().setAuthentication(new OAuth2Authentication(user));		
	
		Bookmark bookmark = bookmarkService.findOrCreateReadLater(new Bookmark(user, 
				new WebUrl(user, "http://forum.spring.io/forum/spring-projects/data/15887-howto-register-a-hibernate-event-listener")));
		
//		Assert.assertTrue(bookmarkedResourceRepository.count() > 0);
//		List<BookmarkedResource> resources = bookmarkedResourceRepository.findAll();
//		for(BookmarkedResource resource: resources) {
//			System.out.println(resource.getContent());
//		}
	}
}
