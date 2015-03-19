package com.gnoht.tlrl.controller;

import java.util.Collection;

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

import com.gnoht.tlrl.controller.support.ResourceResponse;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.Bookmark.ReadLater;
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
	 * @param bookmark to save.
	 * @return saved instance of the Bookmark.
	 */
	@RequestMapping(value={"/urls"}, method=RequestMethod.POST)
	public @ResponseBody Bookmark create(@Valid @RequestBody Bookmark bookmark) {
		LOG.info("Starting request for /api/urls: bookmark={}", bookmark);
		return bookmarkService.create(bookmark);
	}
	
	@RequestMapping(value="/urls/{id}/readlater", method=RequestMethod.PUT)
	public @ResponseBody ReadLater updateReadLater(
				@PathVariable Long id, @RequestBody ReadLater readLater) {
		LOG.info("Starting request for /api/urls/{id}/readlater: id={}, readLater={}", id, readLater);
		Bookmark updated = bookmarkService.update(id, readLater);
		return updated.getReadLater();
	}
	
}
