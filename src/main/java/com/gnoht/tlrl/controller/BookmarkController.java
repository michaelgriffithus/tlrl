package com.gnoht.tlrl.controller;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gnoht.tlrl.controller.support.TargetUser;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.BookmarkResource;
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.BookmarkPageRequest;
import com.gnoht.tlrl.repository.BookmarkResourceRepository;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.BookmarkService;
import com.gnoht.tlrl.service.ReadLaterWebPageService;
import com.gnoht.tlrl.service.UserService;

@RestController
@RequestMapping(value="/api")
public class BookmarkController {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkController.class);

	@Resource private BookmarkService bookmarkService;
	@Resource private BookmarkRepository repo;
	@Resource private ReadLaterWebPageService readLaterWebPageService;
	@Resource private UserService userService;
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	public ResultPage<ReadLaterWebPage> searchAll(
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable,
			@RequestParam(required=true, value="q", defaultValue="*") String terms,
			@RequestParam(required=false, value="tags") String[] tags) {
		
		LOG.debug("pageable.page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
		return readLaterWebPageService.search(toSet(terms), toSet(tags), pageable);
	}
	
	@RequestMapping(value="/urls", method=RequestMethod.GET)
	public ResultPage<Bookmark> findAll(@CurrentUser User currentUser,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable,
			@RequestParam(required=false, value="tags") String[] tags) {
		LOG.info("Starting findAll: ");
		return bookmarkService.findAllTagged(toSet(tags), pageable);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.GET)
	public Page<Bookmark> findAllByWebpage(@PathVariable(value="id") Long id,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		return bookmarkService.findPopularByWebUrl(id);
	}
	
	@RequestMapping(value="/recent", method=RequestMethod.GET)
	public ResultPage<Bookmark> findRecent() {
		return bookmarkService.findRecent(new PageRequest(0, 50));
	}
	
	@RequestMapping(value="/popular", method=RequestMethod.GET)
	public ResultPage<Bookmark> findPopular(
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		return bookmarkService.findPopular(pageable);
	}
	
	/**
	 * Search WebResource index with given term.
	 * @param term Request parameter with term to search for
	 * @return Collection of {@link IndexedWebPage}s or empty list
	 */
	@RequestMapping(value="/@{userName}/search", method=RequestMethod.GET)
	public ResultPage<ReadLaterWebPage> searchAllByUser(@PathVariable String userName,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable,
			@RequestParam(required=true, value="q", defaultValue="*") String terms,
			@RequestParam(required=false, value="tags") String[] tags, @CurrentUser User currentUser) {
		User user = userService.findByName(userName);
		LOG.debug("pageable.page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
		return readLaterWebPageService.search(toSet(terms), toSet(tags), user, currentUser, pageable);
	}
	
	private boolean isOwner(User currentUser, User targetUser) {
		return (currentUser != null && targetUser != null 
				&& currentUser.getId().equals(targetUser.getId()));
	}
	
	/**
	 * Handles all request for querying {@link Bookmark}s owned by a target 
	 * {@link User}. Note: this method is overloaded to handle the request
	 * differently based on who is making the request. If caller and target User 
	 * are same (private allowed), then additional filters may be applied (see below). 
	 * Otherwise it's a public query and only tags filter may be applied in addition 
	 * to the target User.
	 * 
	 * @param currentUser The current authenticated {@link User} for current context
	 * @param targetUser The user to filter by
	 * @param tags List of tags to filter by
	 * @param pageable 
	 * @return
	 */
	@RequestMapping(value={"/@{userName}", "/@{userName}/urls"}, method=RequestMethod.GET)
	public ResultPage<Bookmark> findAllByUser(@CurrentUser User currentUser, 
			@TargetUser("userName") User targetUser, 
			@RequestParam(value="tags", required=false) String[] tags,
			@PageableDefault(page=0, size=20) Pageable pageable) 
	{
		LOG.info("Starting findAllByUser(): currentUser={}, targetUser={}, tags={}, pageable={}",
				currentUser, targetUser, tags, pageable);
		
		if(isOwner(currentUser, targetUser)) {
			BookmarkPageRequest bookmarkPageRequest = new BookmarkPageRequest(pageable);
			LOG.debug("bookmarkPageRequest={}", bookmarkPageRequest);
			return bookmarkPageRequest.hasUntaggedSortProperty() ? 
					bookmarkService.findAllByOwnerAndUntagged(currentUser, bookmarkPageRequest) :
				bookmarkService.findAllByOwnerAndTagged(currentUser, toSet(tags), bookmarkPageRequest);
		} else {
			return bookmarkService.findAllByUserAndTagged(targetUser, toSet(tags), pageable);
		}
	}
	
	@RequestMapping(value="/urls/{id}/status", method=RequestMethod.PUT)
	public Bookmark setReadLaterStatus(@CurrentUser User currentUser,
			@PathVariable("id") Long id, @RequestBody(required=true) Bookmark bookmark) {
		bookmark.setUser(currentUser);
		bookmark.setId(id);
		return bookmarkService.updateReadLaterStatus(bookmark);
	}
	
	/**
	 * Creates a new {@link Bookmark} and returns the newly created instance.
	 * @param webPage WebPage referenced by ReadLater.
	 * @return newly created ReadLater instance
	 */
	@RequestMapping(value="/urls", method=RequestMethod.POST)
	public Bookmark create(@CurrentUser User currentUser,
			@Valid @RequestBody(required=true) Bookmark bookmark) {
		LOG.debug("Starting create(): readLater={}", bookmark);
		bookmark.setUser(currentUser);
		return bookmarkService.findOrCreate(bookmark); //.findOrCreateReadLater(bookmark);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.PUT)
	public Bookmark update(@CurrentUser User currentUser, @PathVariable Long id,
			@Valid @RequestBody(required=true) Bookmark bookmark, BindingResult bindingResult) {
		
		if(bindingResult.hasFieldErrors("tags")) {
			LOG.debug(bookmark.getTags().size() + " tags detected, splicing list!");
			bookmark.setTags(bookmark.getTags().subList(0, 5));
			LOG.debug(bookmark.getTags().size() + " tags after splicing!");
		}
		// check if user owns readlater
		bookmark.setId(id);
		return bookmarkService.update(bookmark);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.DELETE)
	public Bookmark delete(@CurrentUser User currentUser, @PathVariable("id") Long id) {
		LOG.info("Starting delete(): id={}", id);
		bookmarkService.delete(id);
		Bookmark bookmark = new Bookmark();
		bookmark.setId(id);
		return bookmark;
	}
	
	//TODO: move to property editor
	private final Set<String> toSet(String ... items) {
		if(items == null) {
			return Collections.emptySet();
		} else {
			Set<String> itemSet = new HashSet<String>(items.length);
			for(String item: items) 
				itemSet.add(item);
			return itemSet;
		}
	}
	
}
