package com.gnoht.tlrl.repository;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.gnoht.tlrl.domain.Tag;

public interface ResultPage<T> {

	public List<T> getContent();
	public int getNumber();
	public int getNumberOfElements();
	public int getTotalPages();
	public long getTotalElements();
	public int getSize();
	public List<Tag> getAllTags();
	public List<Tag> getRelatedTags();
	public Sort getSort();
}
