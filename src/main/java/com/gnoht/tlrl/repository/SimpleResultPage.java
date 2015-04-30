package com.gnoht.tlrl.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.Tag;

public class SimpleResultPage implements ResultPage<ReadLater> {

	final List<ReadLater> contents;
	final Pageable pageable;
	final ReadLaterStats stats;
	
	public SimpleResultPage(List<ReadLater> contents, ReadLaterStats stats, Pageable pageable) {
		this.contents = contents;
		this.pageable = pageable;
		this.stats = stats;
	}
	
	@Override
	public final List<ReadLater> getContent() {
		return Collections.unmodifiableList(contents);
	}

	@Override
	public int getNumber() {
		return 0;
	}

	@Override
	public int getNumberOfElements() {
		return contents.size();
	}

	@Override
	public int getTotalPages() {
		return 1;
	}

	@Override
	public long getTotalElements() {
		return contents.size();
	}

	@Override
	public int getSize() {
		return pageable.getPageSize();
	}

	@Override
	public List<Tag> getAllTags() {
		return stats.getAllTags();
	}

	@Override
	public List<Tag> getRelatedTags() {
		return Collections.emptyList();
	}

	@Override
	public Sort getSort() {
		return pageable.getSort();
	}

}
