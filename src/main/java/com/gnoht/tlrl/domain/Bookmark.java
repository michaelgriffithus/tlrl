package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="bookmark")
@Table(uniqueConstraints={
	@UniqueConstraint(columnNames={"user_id", "weburl_id"})
})
public class Bookmark extends ManagedAuditable<Long> {

	private static final long serialVersionUID = -1718561876002831254L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String title;
	private String description;
	
	@Column(name="shared", columnDefinition="boolean default false", nullable=false)
	private boolean shared = false;

	@Enumerated(EnumType.STRING)
	@Column(name="read_later_status", nullable=false)
	private Status status = Status.NA;
	
	@ManyToOne(fetch=FetchType.EAGER, targetEntity=WebUrl.class,
			cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="weburl_id")
	@JsonIgnore
	private WebUrl webUrl;
	
	@ManyToMany(targetEntity=Tag.class, fetch=FetchType.EAGER,  
			cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name="bookmark_tags", 
			joinColumns={@JoinColumn(name="bookmark_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	@OrderColumn(name="idx")
	@Size(max=5)
	private List<Tag> tags = new ArrayList<Tag>();
	
	@OneToOne(fetch=FetchType.LAZY, orphanRemoval=true, optional=true, 
			targetEntity=BookmarkResource.class, mappedBy="bookmark")
	@JsonIgnore
	private BookmarkResource resource;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="user_id")
	@JsonIgnore
	private User user;
	
	@Transient
	private int refCount;
	
	public Bookmark() {}
	
	public Bookmark(User user, WebUrl webUrl) {
		this.webUrl = webUrl;
		this.user = user;
		this.title = webUrl.getTitle();
		this.description = webUrl.getDescription();
	}
	
	public Bookmark(String url) {
		this.webUrl = new WebUrl(user, url);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@JsonIgnore
	public void setResource(BookmarkResource resource) {
		this.resource = resource;
	}
	@JsonIgnore
	public BookmarkResource getResource() {
		return this.resource;
	}
	
	//TODO: move to DTO
	@Transient
	@JsonProperty(value="userId")
	public Long getUserId() {
		return (user == null) ? null : user.getId();
	}

	@Transient
	@JsonProperty(value="userName")
	public String getUserName() {
		return (user == null) ? null : user.getName();
	}
	
	//TODO: move to DTO
	public int getRefCount() {
		return refCount;
	}
	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	@JsonProperty
	public String getUrl() {
		return (webUrl == null ? null : webUrl.getUrl());
	}
	public void setUrl(String url) {
		this.webUrl = new WebUrl(url);
	}
	
	public WebUrl getWebPage() {
		return this.webUrl;
	}
	public void setWebPage(WebUrl webUrl) {
		this.webUrl = webUrl;
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@Deprecated
	public Bookmark update(Bookmark from) {
		this.title = from.title;
		this.shared = from.shared;
		this.description = from.getDescription();
		this.tags = from.getTags();
		return this;
	}

	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("webResource", webUrl)
			.add("shared", shared)
			.add("status", status)
			.add("title", title)
			.add("tags", tags)
			.add("description", description)
			.add("url", getUrl());
	}
	
	/**
	 * Status of bookmark.
	 */
	public static enum Status {
		NA, UNREAD, READ;
	}
}
