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
import com.gnoht.tlrl.repository.ManageResultPage;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.repository.SimpleResultPage;
import com.gnoht.tlrl.repository.readlater.BookmarkRepository;
import com.gnoht.tlrl.service.support.ManagedService;


@Service("tlrlService")
public class BookmarkServiceImpl 
			extends ManagedService<Long, Bookmark, BookmarkRepository> 
		implements BookmarkService {
	
	private static final Logger LOG = LoggerFactory.getLogger(BookmarkServiceImpl.class);

	@Resource private BookmarkRepository readLaterRepository;
	@Resource private WebUrlService webUrlService;
	@Resource private ReadLaterWebPageService readLaterWebPageService;

	@Inject
	public BookmarkServiceImpl(BookmarkRepository repository,
			MessageSourceAccessor messageSource) {
		super(repository, messageSource);
	}

	@Override
	public Bookmark findOrCreateReadLater(User user, String url) {
		return findOrCreateReadLater(new Bookmark(user, new WebUrl(user, url)));
	}
	
	@Transactional
	public Bookmark findOrCreateReadLater(Bookmark bookmark) {
		Bookmark existing = readLaterRepository.
				findOneByUserAndWebUrlUrl(bookmark.getUser(), bookmark.getUrl());
		if(existing == null) {
			WebUrl webUrl = webUrlService.findByUrlOrCreate(bookmark.getUrl());
			bookmark.setWebPage(webUrl);
			
			if(bookmark.getTitle() == null && webUrl.getTitle() != null) { 
				bookmark.setTitle(webUrl.getTitle());
			}
			if(bookmark.getDescription() == null && webUrl.getDescription() != null) {
				bookmark.setDescription(webUrl.getDescription());
			}
				
			existing = save(bookmark);
			
			ReadLaterWebPage readLaterWebPage = new ReadLaterWebPage(existing);
//			if(webResource.getContent() != null)
//				readLaterWebPage.setContent(new String(webResource.getContent()));
//			readLaterWebPageService.create(readLaterWebPage);
		}
		return existing;
	}

	public Page<Bookmark> findPopularByWebUrl(Long id) {
		return readLaterRepository.findPopularByWebUrl(id);
	}
	
	@Override
	public ResultPage<Bookmark> findRecent(Pageable pageable) {
		List<Bookmark> bookmarks = readLaterRepository.findRecent(pageable);
		ReadLaterStats stats = readLaterRepository.findRecentTags();
		return new SimpleResultPage(bookmarks, stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findPopular(Pageable pageable) {
		List<Bookmark> bookmarks = readLaterRepository.findPopular(pageable);
		
		ReadLaterStats stats = readLaterRepository.findPopularTags();
		return new SimpleResultPage(bookmarks, stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByOwnerAndTagged(
				User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndTagged(): owner={}, filters={}, tags={}", owner, readLaterQueryFilter, tags);
		
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByOwnerAndTagged(owner, readLaterQueryFilter, tags);
		List<Bookmark> bookmarks = readLaterRepository.
				findAllByOwnerAndTagged(owner, readLaterQueryFilter, tags, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByOwnerAndUntagged(
				User owner, ReadLaterQueryFilter readLaterQueryFilter, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndUntagged(): owner={}, filters={}", owner, readLaterQueryFilter);
		
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByOwnerAndUntagged(owner, readLaterQueryFilter);
		List<Bookmark> bookmarks = readLaterRepository.
				findAllByOwnerAndUntagged(owner, readLaterQueryFilter, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllByUserAndTagged(User user,
			Set<String> tags, Pageable pageable) {
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByUserAndTags(user, tags);
		List<Bookmark> bookmarks = readLaterRepository.
				findAllByUserAndTags(user, tags, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<Bookmark> findAllTagged(Set<String> tags, Pageable pageable) {
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByTags(tags);
		List<Bookmark> bookmarks = readLaterRepository.
				findAllByTags(tags, pageable);
		return new ManageResultPage(new PageImpl<Bookmark>(
				bookmarks, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public Bookmark findReadLaterById(Long id) {
		return readLaterRepository.findOne(id);
	}

	@Transactional(readOnly=false)
	@Override
	public Bookmark updateReadLater(Bookmark updated) {
		Bookmark bookmark = readLaterRepository.findOne(updated.getId());
		
		checkIfOwner(bookmark, updated);
		
		updated = readLaterRepository.save(bookmark.update(updated));
		readLaterWebPageService.update(new ReadLaterWebPage(updated));
		return updated;
	}

	@Transactional(readOnly=false)
	@Override
	public Bookmark updateReadLaterStatus(Bookmark updated) {
		Bookmark bookmark = readLaterRepository.findOne(updated.getId());

		// TODO: need to get current user making call for more robust check.
		// current check rely on fact that only caller is safe
		checkIfOwner(bookmark, updated);
		
		bookmark.setReadLaterStatus(updated.getReadLaterStatus());
		bookmark = readLaterRepository.save(bookmark);
		readLaterWebPageService.update(new ReadLaterWebPage(bookmark));
		return bookmark;
	}

	private void checkIfOwner(Bookmark stored, Bookmark updated) {
		if(!(stored != null && updated != null && updated.getUser() != null 
				&& stored.getUserId().equals(updated.getUserId()))) {
			throw new RuntimeException("Not the owner!");
		}
	}
	
	
	@Override
	public Bookmark deleteReadLater(Bookmark bookmark) {
		delete(bookmark.getId());
		readLaterWebPageService.delete(bookmark.getId().toString());
//		Bookmark bookmark = readLaterRepository.findOne(toDelete.getId());
//		if(bookmark != null && bookmark.getUserId().equals(toDelete.getUserId())) {
//			readLaterRepository.delete(bookmark);
//			readLaterWebPageService.delete(bookmark.getId().toString());
			return bookmark;
//		}
//		return null;
	}
}
