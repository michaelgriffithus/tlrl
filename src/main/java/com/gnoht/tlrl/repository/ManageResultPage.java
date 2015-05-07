package com.gnoht.tlrl.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.Tag;

public class ManageResultPage implements ResultPage<Bookmark> {

	private Page<Bookmark> page;
	private final Pageable pageable;
	private final ReadLaterStats stats;
	
	public ManageResultPage(Page<Bookmark> page, ReadLaterStats stats, Pageable pageable) {
		this.page = page;
		this.pageable = pageable;
		this.stats = stats;
	}
	
	@Override
	public List<Bookmark> getContent() {
		return page.getContent();
	}

	@Override
	public int getNumber() {
		return page.getNumber();
	}

	@Override
	public int getNumberOfElements() {
		return page.getNumberOfElements();
	}

	@Override
	public int getTotalPages() {
		return page.getTotalPages();
	}

	@Override
	public long getTotalElements() {
		return page.getTotalElements();
	}

	@Override
	public int getSize() {
		return page.getSize();
	}

	@Override
	public List<Tag> getAllTags() {
		return stats.getAllTags();
	}

	@Override
	public List<Tag> getRelatedTags() {
		return stats.getRelatedTags();
	}

	@Override
	public Sort getSort() {
		return page.getSort();
	}

}
