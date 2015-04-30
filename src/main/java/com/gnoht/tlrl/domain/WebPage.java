package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="webpage")
public class WebPage extends WebResource<String, Long, WebPage> {
	
	private static final long serialVersionUID = 5423495253286527912L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(unique=true)
	private String url;
	private String title;

	@Column(name="fetched", columnDefinition="boolean default false", nullable=false)
	private boolean fetched = false;
	
	@Transient
	private int refCount;
	
	@Column(columnDefinition="TEXT")
	private String description;
	
	@Column(columnDefinition="TEXT")
	private String content;
	
	@ManyToMany(targetEntity=Tag.class)
	@JoinTable(name="webpage_tags", 
			joinColumns={@JoinColumn(name="webpage_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	private Set<Tag> tags = new HashSet<Tag>();
	
	@OneToMany(mappedBy="webPage", fetch=FetchType.LAZY,
			targetEntity=ReadLater.class,
			cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private Set<ReadLater> readLaters = new HashSet<ReadLater>();

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="user_id")
	private User user;
	
	@Temporal(TemporalType.DATE)
	private Date dateCreated;
	
	@Temporal(TemporalType.DATE)
	private Date dateModified;
	
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
	public Set<ReadLater> getReadLaters() {
		return readLaters;
	}
	public void setReadLaters(Set<ReadLater> readLaters) {
		this.readLaters = readLaters;
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	public int getRefCount() {
		return refCount;
	}
	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}
	@PrePersist
	protected void onPersist() {
		dateCreated = new Date();
	}
	@PreUpdate
	protected void onUpdate() {
		dateModified = new Date();
	}
	public boolean isFetched() {
		return fetched;
	}
	public void setFetched(boolean fetched) {
		this.fetched = fetched;
	}

	@Override
	public WebPage update(WebPage from) {
		return this;
	}
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper();
	}
}
