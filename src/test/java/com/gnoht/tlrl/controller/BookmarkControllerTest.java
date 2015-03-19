package com.gnoht.tlrl.controller;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.Bookmark.ReadLater;
import com.gnoht.tlrl.domain.Bookmark.SharedStatus;
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
		// mocking bookmark service 
		when(bookmarkService.create(any(Bookmark.class)))
			.then(new Answer<Bookmark>() {
				@Override
				public Bookmark answer(InvocationOnMock inv) throws Throwable {
					Bookmark saved = Bookmark
						.updater(inv.getArgumentAt(0, Bookmark.class))
							.id(1L).get(); 			// saved bookmarks will have id and 
					saved.setDateCreated();	// created timestamp
					return saved;
				}});
		
		// bookmark to save
		Bookmark toCreate = createTestBookmark();
		// make sure we have no id and created timestamp
		assertTrue("Bookmark is not new!", (toCreate.getId() == null && toCreate.getDateCreated() == null));
		
		this.mockMvc.perform(
			post("/api/urls")
				.content(toJson(toCreate))
				.contentType(contentType))
			.andExpect(jsonPath("$.id", is(1)))
			.andExpect(jsonPath("$.dateCreated", notNullValue()))
			// test default readlater and shared status
			.andExpect(jsonPath("$.readLater", is(ReadLater.NA.name())))
			.andExpect(jsonPath("$.shared", is(SharedStatus.PRIVATE.value())));
	}

	@Test
	public void shouldUpdateSharedStatusAndReadLater() throws IOException, Exception {
		final Bookmark toUpdate = createTestBookmark();
		
		when(bookmarkService.update(any(Long.class), any(ReadLater.class)))
			.then(new Answer<Bookmark>() {
				@Override
				public Bookmark answer(InvocationOnMock inv) throws Throwable {
					return Bookmark.updater(toUpdate)
							.readLater(inv.getArgumentAt(1, ReadLater.class))
							.get();
				}
			});
		
		assertEquals("ReadLater is not in default state!", ReadLater.NA, toUpdate.getReadLater());
		assertEquals("PRIVATE should be default status!", SharedStatus.PRIVATE.value(), toUpdate.isShared());
		
		this.mockMvc.perform(
				put("/api/urls/1/readlater")
					.contentType(contentType)
					.content(toJson(ReadLater.UNREAD)))
				.andExpect(jsonPath("$", is(ReadLater.UNREAD.name())));
		
	}

	Bookmark createTestBookmark() {
		return Bookmark.builder("http://yahoo.com", new User(1L)).get();
	}
	
	@Override
	protected BookmarkController createController() {
		return new BookmarkController();
	}
}
