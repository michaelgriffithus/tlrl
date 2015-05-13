package com.gnoht.tlrl.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Transient;

import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="webpage")
public class WebPage extends ManagedAuditable<Long> {
	
	private static final long serialVersionUID = 5423495253286527912L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(columnDefinition="text", unique=true, nullable=false, updatable=false)
	private String url;
	
	@Column(columnDefinition="text", nullable=true)
	private String description;
	
	@Column(columnDefinition="text", nullable=true)
	private String title;

	@Column(name="fetched", columnDefinition="boolean default false", nullable=false)
	private boolean fetched = false;
	
	@Transient
	private int refCount;
	
	@Column(nullable=true) @Lob
	private byte[] content;
	
	@ManyToMany(targetEntity=Tag.class)
	@JoinTable(name="webpage_tags", 
			joinColumns={@JoinColumn(name="webpage_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	private Set<Tag> tags = new HashSet<Tag>();
	
	@OneToMany(mappedBy="webPage", fetch=FetchType.LAZY,
			targetEntity=Bookmark.class,
			cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private Set<Bookmark> bookmarks = new HashSet<Bookmark>();

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.REFRESH}, optional=false)
	@JoinColumn(name="user_id")
	private User user;
	
	public WebPage() {}

	public WebPage(String url) {
		this(null, url);
	}
	
	public WebPage(User user, String url) {
		this.user = user;
		this.url = url;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUrl() {
		return url;
	}
	public Set<Bookmark> getReadLaters() {
		return bookmarks;
	}
	public void setReadLaters(Set<Bookmark> bookmarks) {
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
	
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	public int getRefCount() {
		return refCount;
	}
	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}
	public boolean isFetched() {
		return fetched;
	}
	public void setFetched(boolean fetched) {
		this.fetched = fetched;
	}

	public WebPage update(WebPage from) {
		return this;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper();
	}
}
