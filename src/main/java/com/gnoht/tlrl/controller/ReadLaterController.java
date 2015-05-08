package com.gnoht.tlrl.controller;


import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.repository.readlater.ReadLaterJpaRepository;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.ReadLaterService;
import com.gnoht.tlrl.service.ReadLaterWebPageService;
import com.gnoht.tlrl.service.UserService;

@RestController
@RequestMapping(value="/api")
public class ReadLaterController {

	private static final Logger LOG = LoggerFactory.getLogger(ReadLaterController.class);

	@Resource private ReadLaterService readLaterService;
	@Resource private ReadLaterJpaRepository repo;
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
		return readLaterService.findAllTagged(toSet(tags), pageable);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.GET)
	public WebPage findAllByWebpage(@PathVariable(value="id") Long id,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		return readLaterService.findAllByWebPage(id);
	}
	
	@RequestMapping(value="/recent", method=RequestMethod.GET)
	public ResultPage<Bookmark> findRecent() {
		return readLaterService.findRecent(new PageRequest(0, 50));
	}
	
	@RequestMapping(value="/popular", method=RequestMethod.GET)
	public ResultPage<Bookmark> findPopular(
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		return readLaterService.findPopular(pageable);
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
	 * @param currentUser the current authenticated User making this request
	 * @param user the target User to query by
	 * @param ownerOnlyFilters optional filters to apply against the query. Note, 
	 * these filters will only be applied if calling authenticated User is also 
	 * the target User.
	 * @param tags list of tags to filter by
	 * @param pageable 
	 * @return 
	 */
	@RequestMapping(value={"/@{userName}", "/@{userName}/urls"}, method=RequestMethod.GET)
	public ResultPage<Bookmark> findAllByUser(@CurrentUser User currentUser, @TargetUser("userName") User user, 
			@RequestParam(value="filters", defaultValue="") ReadLaterQueryFilter ownerOnlyFilters, 
			@RequestParam(value="tags", required=false) String[] tags,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		
		LOG.debug("Starting findAllByUser(): currentUser={}, user={}, filters={}, tags={}", 
				currentUser, user, ownerOnlyFilters, tags);
		
		if(!isOwner(currentUser, user)) {
			LOG.debug("In non owner block");
			/* public/non owner queries can only filter by tags */
			return readLaterService.findAllByUserAndTagged(user, toSet(tags), pageable);
		} else {
			LOG.debug("In owner block");
			/* private/owner queries can filter by tags and optional untagged, shared, status */
			return (ownerOnlyFilters.isUntagged() ? 
					readLaterService.findAllByOwnerAndUntagged(currentUser, ownerOnlyFilters, pageable) :
				readLaterService.findAllByOwnerAndTagged(currentUser, ownerOnlyFilters, toSet(tags), pageable));
		}
	}

	@RequestMapping(value="/urls/{id}/status", method=RequestMethod.PUT)
	public Bookmark setReadLaterStatus(@CurrentUser User currentUser,
			@PathVariable("id") Long id, @RequestBody(required=true) Bookmark bookmark) {
		bookmark.setUser(currentUser);
		bookmark.setId(id);
		return readLaterService.updateReadLaterStatus(bookmark);
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
		return readLaterService.findOrCreateReadLater(bookmark);
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
		bookmark.setUser(currentUser); //prevent spoofing
		return readLaterService.updateReadLater(bookmark);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.DELETE)
	public Bookmark delete(@CurrentUser User currentUser, @PathVariable Long id) {
		Bookmark bookmark = new Bookmark();
		bookmark.setId(id);
		bookmark.setUser(currentUser);
		readLaterService.deleteReadLater(bookmark);
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
