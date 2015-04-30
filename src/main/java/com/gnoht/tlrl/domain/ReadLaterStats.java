package com.gnoht.tlrl.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Simple wrapper for stats about a Users collection of {@link ReadLater}s.
 */
public class ReadLaterStats implements Serializable {

	private static final long serialVersionUID = 4180767249781528547L;
	
	private List<Tag> allTags = Collections.emptyList();
	private List<Tag> relatedTags = Collections.emptyList();
	private Integer totalReadLaters;
	
	public ReadLaterStats() {}
	
	public ReadLaterStats(List<Tag> allTags, List<Tag> relatedTags, int totalReadLaters) {
		this.allTags = allTags;
		this.totalReadLaters = totalReadLaters;
		this.relatedTags = relatedTags;
	}
	
	public final List<Tag> getAllTags() {
		return Collections.unmodifiableList(allTags);
	}
	public void setAllTags(List<Tag> allTags) {
		this.allTags = allTags;
	}
	public final List<Tag> getRelatedTags() {
		return Collections.unmodifiableList(relatedTags);
	}
	public void setRelatedTags(List<Tag> relatedTags) {
		this.relatedTags = relatedTags;
	}
	public Integer getTotalReadLaters() {
		return totalReadLaters;
	}
	public void setTotalReadLaters(Integer totalReadLaters) {
		this.totalReadLaters = totalReadLaters;
	}
}
