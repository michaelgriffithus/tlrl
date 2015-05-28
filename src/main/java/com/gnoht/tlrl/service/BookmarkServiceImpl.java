package com.gnoht.tlrl.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.controller.ReadLaterQueryFilter;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.repository.BookmarkPageRequest;
import com.gnoht.tlrl.repository.ManageResultPage;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.repository.SimpleResultPage;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;
import com.gnoht.tlrl.security.SecurityUtils;
import com.gnoht.tlrl.service.support.ManagedService;


@Service("tlrlService")
public class BookmarkServiceImpl 
			extends ManagedService<Long, Bookmark, BookmarkRepository> 
		implements BookmarkService {
	
	private static final Logger LOG = LoggerFactory.getLogger(BookmarkServiceImpl.class);

	@Resource private WebUrlService webUrlService;
	@Resource private ReadLaterWebPageService readLaterWebPageService;

	@Inject
	public BookmarkServiceImpl(BookmarkRepository repository,
				MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}
	
	/*
	 * @see com.gnoht.tlrl.service.BookmarkService#findOrCreate(
	 * 	com.gnoht.tlrl.domain.Bookmark)
	 */
	@Transactional
	@Override
	public Bookmark findOrCreate(Bookmark bookmark) {
		LOG.info("Starting findOrCreate(): bookmark={}", bookmark);
		
		User user = SecurityUtils.getCurrentUser();
		
		// See if we have an existing bookmark already for current user
		Bookmark toReturn = getRepository()
				.findOneByUserAndWebUrlUrl(user, bookmark.getUrl());
		
		if(toReturn == null) {
			return create(bookmark);
		} else {
			LOG.debug("Found existing bookmark={}", bookmark);
			return toReturn;
		}
	}

	/*
	 * @see com.gnoht.tlrl.service.support.ManagedService#create(
	 * 	com.gnoht.tlrl.domain.support.Manageable)
	 */
	@Transactional
	@Override
	public Bookmark create(Bookmark bookmark) {
		LOG.info("Starting create(): bookmark={}", bookmark);
		
		User user = SecurityUtils.getCurrentUser();
		
		WebUrl webUrl = webUrlService.findByUrlOrCreate(bookmark.getUrl());
		bookmark.setWebPage(webUrl);
		bookmark.setUser(user);
		
		// some defaults if new bookmark attrs are empty
		if(bookmark.getTitle() == null) 
			bookmark.setTitle(webUrl.getTitle());
		if(bookmark.getDescription() == null) 
			bookmark.setDescription(webUrl.getDescription());
		
		return super.create(bookmark);
	}

	public Page<Bookmark> findPopularByWebUrl(Long id) {
		return getRepository().findPopularByWebUrl(id);
	}
	
	@Override
	public ResultPage<Bookmark> findRecent(Pageable pageable) {
		List<Bookmark> bookmarks = getRepository().findRecent(pageable);
		ReadLaterStats stats = getRepository().findRecentTags();
		return new SimpleResultPage(bookmarks, stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findPopular(Pageable pageable) {
		List<Bookmark> bookmarks = getRepository().findPopular(pageable);
		
		ReadLaterStats stats = getRepository().findPopularTags();
		return new SimpleResultPage(bookmarks, stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByOwnerAndTagged(
				User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndTagged(): owner={}, filters={}, tags={}", owner, readLaterQueryFilter, tags);
		
		ReadLaterStats stats = getRepository().
				findReadLaterStatsByOwnerAndTagged(owner, readLaterQueryFilter, tags);
		List<Bookmark> bookmarks = getRepository().
				findAllByOwnerAndTagged(owner, readLaterQueryFilter, tags, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByOwnerAndUntagged(
				User owner, ReadLaterQueryFilter readLaterQueryFilter, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndUntagged(): owner={}, filters={}", owner, readLaterQueryFilter);
		
		ReadLaterStats stats = getRepository().
				findReadLaterStatsByOwnerAndUntagged(owner, readLaterQueryFilter);
		List<Bookmark> bookmarks = getRepository().
				findAllByOwnerAndUntagged(owner, readLaterQueryFilter, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	
	
	@Override
	public ResultPage<Bookmark> findAllByOwnerAndTagged2(User owner,
			Set<String> tags, BookmarkPageRequest pageable) {
		ReadLaterStats stats = getRepository().findAllMetaByOwnerAndTagged(owner, tags);
		List<Bookmark> bookmarks = getRepository().findAllByOwnerAndTagged(owner, tags, pageable);
		return new ManageResultPage(
				new PageImpl<>(bookmarks, pageable, stats.getTotalReadLaters()), 
				stats, 
				pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByOwnerAndUntagged2(User owner,
			BookmarkPageRequest pageable) {
		ReadLaterStats stats = getRepository().findAllMetaByOwnerAndUntagged(owner);
		List<Bookmark> bookmarks = getRepository()
				.findAllByOwnerAndUntagged(owner, pageable);
		return new ManageResultPage(new PageImpl<>(bookmarks, pageable, stats.getTotalReadLaters()), 
				stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByUserAndTagged(User user,
			Set<String> tags, Pageable pageable) {
		ReadLaterStats stats = getRepository().
				findReadLaterStatsByUserAndTags(user, tags);
		List<Bookmark> bookmarks = getRepository().
				findAllByUserAndTags(user, tags, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllTagged(Set<String> tags, Pageable pageable) {
		ReadLaterStats stats = getRepository().
				findReadLaterStatsByTags(tags);
		List<Bookmark> bookmarks = getRepository().
				findAllByTags(tags, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	/*
	 * @see com.gnoht.tlrl.service.support.ManagedService#update(
	 * 	com.gnoht.tlrl.domain.support.Manageable)
	 */
	@Transactional(readOnly=false)
	@Override
	public Bookmark update(Bookmark toUpdate) {
		LOG.info("Starting update(): toUpdate={}", toUpdate);
		
		User user = SecurityUtils.getCurrentUser();
		Bookmark bookmark = getRepository().findOneByUserAndId(user, toUpdate.getId());
		bookmark.setTitle(toUpdate.getTitle());
		bookmark.setDescription(toUpdate.getDescription());
		bookmark.setShared(toUpdate.isShared());
		bookmark.setTags(toUpdate.getTags());
		return getRepository().save(bookmark); 
	}

	/*
	 * @see com.gnoht.tlrl.service.BookmarkService#updateReadLaterStatus(
	 * 	com.gnoht.tlrl.domain.Bookmark)
	 */
	@Transactional(readOnly=false)
	@Override
	public Bookmark updateReadLaterStatus(Bookmark toUpdate) {
		LOG.info("Starting updateReadLaterStatus(): toUpdate={}", toUpdate);
		
		User user = SecurityUtils.getCurrentUser();
		Bookmark bookmark = getRepository().findOneByUserAndId(user, toUpdate.getId());
		bookmark.setReadLaterStatus(toUpdate.getReadLaterStatus());
		return getRepository().save(bookmark);
	}
	
}
