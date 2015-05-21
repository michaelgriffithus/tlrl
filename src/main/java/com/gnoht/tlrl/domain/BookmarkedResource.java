package com.gnoht.tlrl.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Represents the underlying resource (i.e, webpage, pdf, ..) referenced by 
 * a given {@link Bookmark}.  
 */
@Entity
public class BookmarkedResource extends ManagedAuditable<Long> {

	private static final long serialVersionUID = -8872717866719466925L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@OneToOne(fetch=FetchType.LAZY, targetEntity=Bookmark.class,
			cascade={CascadeType.REMOVE}, optional=true)
	@JoinColumn(name="bookmark_id", unique=true, updatable=false)
	private Bookmark bookmark;
	
	@Column(nullable=true) @Lob
	private byte[] content;
	
	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Bookmark getBookmark() {
		return bookmark;
	}
	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public void setContent(String content) {
		this.content = content.getBytes();
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("bookmark", bookmark == null ? bookmark : bookmark.getId())
			.add("content", content == null ? content : content.length);
	}
}
