package com.gnoht.tlrl.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.BookmarkService;

/**
 * {@link BookmarkController} unit test.
 */
public class BookmarkControllerTest 
		extends StandaloneControllerTest<BookmarkController> {

	@Mock
	BookmarkService bookmarkService;
	
	@Test
	public void shouldCreateBookmarkAndReturnIt() throws Exception {
		when(bookmarkService.create(any(Bookmark.class)))
			.then(new Answer<Bookmark>() {
				@Override
				public Bookmark answer(InvocationOnMock invocation) throws Throwable {
					Bookmark saved = Bookmark
						.updater(invocation.getArgumentAt(0, Bookmark.class))
							.id(1L)
							.get();
					saved.setDateCreated();
					return saved;
				}
			});
		 
		Bookmark toCreate = Bookmark
				.builder("http://yahoo.com", new User(1L)).get();

		assertTrue("Bookmark is not new!", (toCreate.getId() == null && toCreate.getDateCreated() == null));
		
		this.mockMvc.perform(
			post("/api/urls")
				.content(toJson(toCreate))
				.contentType(contentType))
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.dateCreated", notNullValue()));
	}

	@Override
	protected BookmarkController createController() {
		return new BookmarkController();
	}
}
