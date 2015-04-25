package com.gnoht.tlrl.controller.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.Bookmark.ReadLater;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.BookmarkService;

@RestController
@RequestMapping(value={"/api"})
public class BookmarkController {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkController.class);
	
	@Resource
	private BookmarkService bookmarkService;
	
	@RequestMapping(value={"/urls"}, method=RequestMethod.GET)
	public @ResponseBody Page<Bookmark> list(HttpServletRequest request) {
		return bookmarkService.findAll(new PageRequest(0, 10));
	}
	
	/**
	 * Create a new {@link Bookmark}. Only requirement is a valid url.
	 * 
	 * @param bookmark The Bookmark to save.
	 * @return saved instance of the Bookmark.
	 */
	@RequestMapping(value={"/urls"}, method=RequestMethod.POST)
	public @ResponseBody Bookmark create(@CurrentUser User user, 
			@Valid @RequestBody(required=true) Bookmark bookmark) {
		LOG.info("Starting request for /api/urls: user={}, bookmark={}", user, bookmark);
		return bookmarkService.findOrCreate(
			Bookmark.updater(bookmark)
				.user(user)
				.get());
	}
	
	/**
	 * Updates {@link ReadLater} state for {@link Bookmark} with given id.
	 * 
	 * @param id of Bookmark to update
	 * @param readLater The ReadLater state to update with
	 * @return ReadLater state from updated Bookmark
	 */
	@RequestMapping(value="/urls/{id}/readlater", method=RequestMethod.PUT)
	public @ResponseBody ReadLater updateReadLater(
				@PathVariable Long id, @RequestBody ReadLater readLater) {
		LOG.info("Starting request for /api/urls/{id}/readlater: id={}, readLater={}", id, readLater);
		Bookmark updated = bookmarkService.update(id, readLater);
		return updated.getReadLater();
	}
	
	/**
	 * Updates shared property for {@link Bookmark} with given id.
	 * 
	 * @param id Id of Bookmark to update
	 * @param status The shared status to update to
	 * @return SharedStatus from updated Bookmark
	 */
	@RequestMapping(value="/urls/{id}/shared", method=RequestMethod.PUT)
	public @ResponseBody Boolean updateSharedStatus(
				@PathVariable Long id, @RequestBody Boolean shared) {
		LOG.info("Starting request for /api/urls/{id}/shared: id={}, shared={}", id, shared);
		return bookmarkService.update(id, shared).isShared();
	}
	
	/**
	 * Updates properties for {@link Bookmark} with given id.
	 * 
	 * @param id Id of Bookmark to update
	 * @param bookmark The Bookmark containing updated properties
	 * @return Bookmark that was updated
	 */
	@RequestMapping(value="/urls/{id}", method=RequestMethod.PUT)
	public @ResponseBody Bookmark update(
				@PathVariable Long id, @RequestBody Bookmark bookmark) {
		LOG.info("Starting request for /api/urls/{id}: id={}, bookmark={}", id, bookmark);
		return bookmarkService.update(bookmark);
	}
	
	/**
	 * Deletes a {@link Bookmark} with given id.
	 * 
	 * @param id Id of Bookmark to delete
	 * @return the Id of the deleted Bookmark
	 */
	@RequestMapping(value="/urls/{id}", method=RequestMethod.DELETE)
	public @ResponseBody Long delete(@PathVariable Long id) {
		LOG.info("Starting request for /api/urls/{id}: id={}", id);
		return bookmarkService.delete(id);
	}
}
