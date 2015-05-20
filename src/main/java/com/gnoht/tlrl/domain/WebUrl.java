package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.gnoht.tlrl.repository.BookmarkListener;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity
@Table(name="weburl")
public class WebUrl extends ManagedAuditable<Long> {
	
	private static final long serialVersionUID = 5423495253286527912L;
	
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="weburl_seq")
	@SequenceGenerator(sequenceName="weburl_id_seq", initialValue=1000, name="weburl_seq")
	private Long id;
	
	@Column(columnDefinition="text", unique=true, nullable=false, updatable=false)
	private String url;
	
	@Column(columnDefinition="text", nullable=true)
	private String description;
	
	@Column(columnDefinition="text", nullable=true)
	private String title;

	@Transient
	private int refCount;
	
	@OneToMany(mappedBy="webUrl", fetch=FetchType.LAZY,
			targetEntity=Bookmark.class,
			cascade={CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	private Collection<Bookmark> bookmarks = new ArrayList<>();

	public WebUrl() {}

	public WebUrl(String url) {
		this(null, url);
	}
	
	public WebUrl(User user, String url) {
		//this.user = user;
		this.url = url;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public Collection<Bookmark> getBookmarks() {
		return bookmarks;
	}
	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getRefCount() {
		return refCount;
	}
	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}
	//TODO: move to DTO
	@Transient
	@JsonProperty(value="userId")
	public Long getUserId() {
		return null;
		//return (user == null) ? null : user.getId();
	}

	@Transient
	@JsonProperty(value="userName")
	public String getUserName() {
		return null;
		//return (user == null) ? null : user.getName();
	}

	public WebUrl update(WebUrl from) {
		return this;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper();
	}
}
