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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gnoht.tlrl.controller.support.TargetUser;
import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.ReadLaterService;
import com.gnoht.tlrl.service.ReadLaterWebPageService;
import com.gnoht.tlrl.service.UserService;

@RestController
@RequestMapping(value="/api")
public class ReadLaterController {

	private static final Logger LOG = LoggerFactory.getLogger(ReadLaterController.class);

	@Resource private ReadLaterService readLaterService;
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
	public ResultPage<ReadLater> findAll(@CurrentUser User currentUser,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable,
			@RequestParam(required=false, value="tags") String[] tags) {
		return readLaterService.findAllTagged(toSet(tags), pageable);
		//return readLaterService.findAllReadLaterByTags(toSet(tags), pageable);
	}
	
	@RequestMapping(value="/url/{id}", method=RequestMethod.GET)
	public WebPage findAllByWebpage(@PathVariable(value="id") Long id,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		return readLaterService.findAllByWebPage(id);
	}
	
	@RequestMapping(value="/recent", method=RequestMethod.GET)
	public ResultPage<ReadLater> findRecent() {
		return readLaterService.findRecent(new PageRequest(0, 50));
	}
	
	@RequestMapping(value="/popular", method=RequestMethod.GET)
	public ResultPage<ReadLater> findPopular(
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
	
	@RequestMapping(value={"/@{userName}", "/@{userName}/urls"}, method=RequestMethod.GET)
	public ResultPage<ReadLater> findAllByUser(@CurrentUser User currentUser, 
			@TargetUser("userName") User user, @FiltersArgument("filters") Filters filters, 
			@RequestParam(value="tags", required=false) String[] tags,
			@PageableDefault(page=0, size=10, sort={"id"}, direction=Direction.ASC) Pageable pageable) {
		
		LOG.debug("Starting findAllByUser(): currentUser={}, user={}, filters={}, tags={}", 
				currentUser, user, filters, tags);
		
		if(!isOwner(currentUser, user)) {
			LOG.debug("In non owner block");
			/* Non owner queries are limited to tagged and public-only readLaters */
			return readLaterService.findAllByUserAndTagged(user, toSet(tags), pageable);
		} else {
			LOG.debug("In owner block");
			/* Owner queries can be tagged or untagged, with public-only/private-only/both readLaters */
			return (filters.isUntagged() ? 
					readLaterService.findAllByOwnerAndUntagged(currentUser, filters, pageable) :
				readLaterService.findAllByOwnerAndTagged(currentUser, filters, toSet(tags), pageable));
		}
	}

	@RequestMapping(value="/urls/{id}/status", method=RequestMethod.PUT)
	public ReadLater setReadLaterStatus(@CurrentUser User currentUser,
			@PathVariable("id") Long id, @RequestBody(required=true) ReadLater readLater) {
		readLater.setUser(currentUser);
		readLater.setId(id);
		return readLaterService.updateReadLaterStatus(readLater);
	}
	
	/**
	 * Creates a new {@link ReadLater} and returns the newly created instance.
	 * @param webPage WebPage referenced by ReadLater.
	 * @return newly created ReadLater instance
	 */
	@RequestMapping(value="/urls", method=RequestMethod.POST)
	public ReadLater create(@CurrentUser User currentUser,
			@Valid @RequestBody(required=true) ReadLater readLater) {
		LOG.debug("Starting create(): readLater={}", readLater);
		readLater.setUser(currentUser);
		return readLaterService.findOrCreateReadLater(readLater);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.PUT)
	public ReadLater update(@CurrentUser User currentUser, @PathVariable Long id,
			@Valid @RequestBody(required=true) ReadLater readLater, BindingResult bindingResult) {
		
		if(bindingResult.hasFieldErrors("tags")) {
			LOG.debug(readLater.getTags().size() + " tags detected, splicing list!");
			readLater.setTags(readLater.getTags().subList(0, 5));
			LOG.debug(readLater.getTags().size() + " tags after splicing!");
		}
		// check if user owns readlater
		readLater.setId(id);
		readLater.setUser(currentUser); //prevent spoofing
		return readLaterService.updateReadLater(readLater);
	}
	
	@RequestMapping(value="/urls/{id}", method=RequestMethod.DELETE)
	public ReadLater delete(@CurrentUser User currentUser, @PathVariable Long id) {
		ReadLater readLater = new ReadLater();
		readLater.setId(id);
		readLater.setUser(currentUser);
		readLaterService.deleteReadLater(readLater);
		return readLater;
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
