package com.gnoht.tlrl.controller.api;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MvcResult;

import com.gnoht.tlrl.controller.BookmarkController;
import com.gnoht.tlrl.controller.StandaloneControllerTest;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.Tag;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.service.ReadLaterService;

/**
 * {@link BookmarkController} unit test.
 */
public class BookmarkControllerTest 
		extends StandaloneControllerTest<BookmarkController> {

//	@Mock ReadLaterService bookmarkService;
//	
//	@Test
//	public void shouldDeleteBookmark() throws Exception {
//		when(bookmarkService.deleteReadLater(any(ReadLater.class)))
//			.thenReturn(1L);
//		
//		mockMvc.perform(
//			delete("/api/urls/1")
//				.contentType(contentType));
//			//.andExpect(arg0)
//	}
//	
//	@Test
//	public void shouldCreateBookmarkAndReturnIt() throws Exception {
//		// mocking bookmark service 
//		when(bookmarkService.findOrCreateReadLater(any(ReadLater.class)))
//			.then(new Answer<ReadLater>() {
//				@Override
//				public ReadLater answer(InvocationOnMock inv) throws Throwable {
//					ReadLater saved = (ReadLater)inv.getArguments()[0];//
//					saved.setId(1L);		// saved bookmarks will have id and 
//					saved.setDateCreated(new Date());
//					saved.setDateModified(new Date());
//					return saved;
//				}});
//		
//		// bookmark to save
//		Bookmark toCreate = createTestBookmark();
//		// make sure we have no id and created timestamp
//		assertTrue("Bookmark is not new!", (toCreate.getId() == null && toCreate.getDateCreated() == null));
//		
//		this.mockMvc.perform(
//			post("/api/urls")
//				.content(toJson(toCreate))
//				.contentType(contentType))
//			.andExpect(jsonPath("$.id", is(1)))
//			.andExpect(jsonPath("$.dateCreated", notNullValue()))
//			// test default readlater and shared status
//			.andExpect(jsonPath("$.readLater", is(ReadLater.NA.name())));
//	}
//
//	@Test
//	public void shouldUpdateReadLater() throws IOException, Exception {
//		final Bookmark toUpdate = createTestBookmark();
//		
//		when(bookmarkService.update(any(Long.class), any(ReadLater.class)))
//			.then(new Answer<Bookmark>() {
//				@Override
//				public Bookmark answer(InvocationOnMock inv) throws Throwable {
//					return Bookmark.updater(toUpdate)
//							.readLater(inv.getArgumentAt(1, ReadLater.class)).get();
//				}});
//		
//		// make sure we're in default state
//		assertEquals(ReadLater.NA, toUpdate.getReadLater());
//		
//		this.mockMvc.perform(
//				put("/api/urls/1/readlater")
//					.contentType(contentType)
//					.content(toJson(ReadLater.UNREAD)))
//				.andExpect(jsonPath("$", is(ReadLater.UNREAD.name())));
//	}
//
//	@Test
//	public void shouldUpdateSharedStatus() throws IOException, Exception {
//		final Bookmark toUpdate = createTestBookmark();
//		
//		when(bookmarkService.update(any(Long.class), any(Boolean.class)))
//			.then(new Answer<Bookmark>() {
//				@Override
//				public Bookmark answer(InvocationOnMock inv) throws Throwable {
//					return Bookmark.updater(toUpdate)
//						.shared(inv.getArgumentAt(1, Boolean.class)).get();
//				}
//			});
//		
//		// make sure to start with default shared status
//		assertEquals(Bookmark.DEFAULT_SHARED_STATUS, toUpdate.isShared());
//		
//		this.mockMvc.perform(
//				put("/api/urls/1/shared")
//					.contentType(contentType)
//					.content(toJson(!Bookmark.DEFAULT_SHARED_STATUS)))
//				.andExpect(jsonPath("$", is(!Bookmark.DEFAULT_SHARED_STATUS)));
//				
//	}
//	
//	@Test
//	public void shouldUpdateBookmark() throws IOException, Exception {
//		final Bookmark existing = createTestBookmark();
//		final Bookmark toUpdate = Bookmark
//				.builder(existing.getUrl(), existing.getUser())
//				.description("a description")
//				.title("a webresource title")
//				.tag(new Tag("tag1"))
//				.get();
//		
//		when(bookmarkService.update(any(Bookmark.class)))
//			.then(new Answer<Bookmark>() {
//				@Override
//				public Bookmark answer(InvocationOnMock inv) throws Throwable {
//					Bookmark given = inv.getArgumentAt(0, Bookmark.class);
//					return Bookmark.updater(existing).id(given.getId())
//							.description(given.getDescription())
//							.tags(given.getTags())
//							.title(given.getTitle())
//							.get();
//				}
//			});
//		
//		MvcResult result = mockMvc.perform(
//			put("/api/urls/1")
//				.contentType(contentType)
//				.content(toJson(toUpdate)))
//			.andExpect(jsonPath("$.title", is(toUpdate.getTitle())))
//			.andExpect(jsonPath("$.description", is(toUpdate.getDescription())))
//			.andReturn();
//		
//		Bookmark returned = (Bookmark) fromJson(
//				Bookmark.class, result.getResponse().getContentAsByteArray());
//		
//		// check we back same tag we saved 
//		assertEquals(returned.getTags().get(0), toUpdate.getTags().get(0));
//	}
//	
//	Bookmark createTestBookmark() {
//		return Bookmark.builder("http://yahoo.com", new User(1L)).get();
//	}
//	
	@Override
	protected BookmarkController createController() {
		return new BookmarkController();
	}
}
