package com.gnoht.tlrl.service;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.controller.Filters;
import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.SharedStatus;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.repository.ManageResultPage;
import com.gnoht.tlrl.repository.ReadLaterJpaRepository;
import com.gnoht.tlrl.repository.ResultPage;
import com.gnoht.tlrl.repository.SimpleResultPage;
import com.gnoht.tlrl.repository.WebPageJpaRepository;


@Service("tlrlService")
public class ReadLaterServiceImpl implements ReadLaterService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReadLaterServiceImpl.class);

	@Resource private ReadLaterJpaRepository readLaterRepository;
	@Resource private WebPageService webPageService;
	@Resource private ReadLaterWebPageService readLaterWebPageService;

	@Override
	public ReadLater findOrCreateReadLater(User user, String url) {
		return findOrCreateReadLater(new ReadLater(user, new WebPage(user, url)));
	}
	
	public ReadLater findOrCreateReadLater(ReadLater readLater) {
		ReadLater existingReadLater = readLaterRepository.
				findOneByUserAndWebPageUrl(readLater.getUser(), readLater.getUrl());
		if(existingReadLater == null) {
			WebPage webPage = webPageService.findOrCreate(readLater.getUser(), readLater.getUrl());
			readLater.setWebPage(webPage);
			
			if(readLater.getTitle() == null && webPage.getTitle() != null) { 
				readLater.setTitle(webPage.getTitle());
			}
			if(readLater.getDescription() == null && webPage.getDescription() != null) {
				readLater.setDescription(webPage.getDescription());
			}
				
			existingReadLater = readLaterRepository.save(readLater);
			
			ReadLaterWebPage readLaterWebPage = new ReadLaterWebPage(existingReadLater);
			readLaterWebPage.setContent(webPage.getContent());
			readLaterWebPageService.create(readLaterWebPage);
		}
		return existingReadLater;
	}

	@Override
	public WebPage findAllByWebPage(Long webPageId) {
		return readLaterRepository.findAllByWebPage(webPageId);
	}

	@Override
	public ResultPage<ReadLater> findRecent(Pageable pageable) {
		List<ReadLater> readLaters = readLaterRepository.findRecent(pageable);
		ReadLaterStats stats = readLaterRepository.findRecentTags();
		return new SimpleResultPage(readLaters, stats, pageable);
	}

	@Override
	public ResultPage<ReadLater> findPopular(Pageable pageable) {
		List<ReadLater> readLaters = readLaterRepository.findPopular(pageable);
		ReadLaterStats stats = readLaterRepository.findPopularTags();
		return new SimpleResultPage(readLaters, stats, pageable);
	}

	@Override
	public ResultPage<ReadLater> findAllByOwnerAndTagged(
				User owner, Filters filters, Set<String> tags, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndTagged(): owner={}, filters={}, tags={}", owner, filters, tags);
		
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByOwnerAndTagged(owner, filters, tags);
		List<ReadLater> readLaters = readLaterRepository.
				findAllByOwnerAndTagged(owner, filters, tags, pageable);
		return new ManageResultPage(new PageImpl<ReadLater>(
				readLaters, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<ReadLater> findAllByOwnerAndUntagged(
				User owner, Filters filters, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndUntagged(): owner={}, filters={}", owner, filters);
		
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByOwnerAndUntagged(owner, filters);
		List<ReadLater> readLaters = readLaterRepository.
				findAllByOwnerAndUntagged(owner, filters, pageable);
		return new ManageResultPage(new PageImpl<ReadLater>(
				readLaters, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<ReadLater> findAllByUserAndTagged(User user,
			Set<String> tags, Pageable pageable) {
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByUserAndTags(user, tags);
		List<ReadLater> readLaters = readLaterRepository.
				findAllByUserAndTags(user, tags, pageable);
		return new ManageResultPage(new PageImpl<ReadLater>(
				readLaters, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ResultPage<ReadLater> findAllTagged(Set<String> tags, Pageable pageable) {
		ReadLaterStats stats = readLaterRepository.
				findReadLaterStatsByTags(tags);
		List<ReadLater> readLaters = readLaterRepository.
				findAllByTags(tags, pageable);
		return new ManageResultPage(new PageImpl<ReadLater>(
				readLaters, pageable, stats.getTotalReadLaters()), stats, pageable);
	}

	@Override
	public ReadLater findReadLaterById(Long id) {
		return readLaterRepository.findOne(id);
	}

	@Transactional(readOnly=false)
	@Override
	public ReadLater updateReadLater(ReadLater updated) {
		ReadLater readLater = readLaterRepository.findOne(updated.getId());
		
		checkIfOwner(readLater, updated);
		
		updated = readLaterRepository.save(readLater.update(updated));
		readLaterWebPageService.update(new ReadLaterWebPage(updated));
		return updated;
	}

	@Transactional(readOnly=false)
	@Override
	public ReadLater updateReadLaterStatus(ReadLater updated) {
		ReadLater readLater = readLaterRepository.findOne(updated.getId());

		// TODO: need to get current user making call for more robust check.
		// current check rely on fact that only caller is safe
		checkIfOwner(readLater, updated);
		
		readLater.setReadLaterStatus(updated.getReadLaterStatus());
		readLater = readLaterRepository.save(readLater);
		readLaterWebPageService.update(new ReadLaterWebPage(readLater));
		return readLater;
	}

	private void checkIfOwner(ReadLater stored, ReadLater updated) {
		if(!(stored != null && updated != null && updated.getUser() != null 
				&& stored.getUserId().equals(updated.getUserId()))) {
			throw new RuntimeException("Not the owner!");
		}
	}
	
	
	@Override
	public ReadLater deleteReadLater(ReadLater toDelete) {
		ReadLater readLater = readLaterRepository.findOne(toDelete.getId());
		if(readLater != null && readLater.getUserId().equals(toDelete.getUserId())) {
			readLaterRepository.delete(readLater);
			readLaterWebPageService.delete(readLater.getId().toString());
			return readLater;
		}
		return null;
	}
}
