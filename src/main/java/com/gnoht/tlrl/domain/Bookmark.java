package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.domain.support.Managed;
import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

@Entity(name="bookmark")
@Table(uniqueConstraints={
	@UniqueConstraint(columnNames={"user_id", "webresource_id"})
})
public class Bookmark extends ManagedAuditable<Long> {

	private static final long serialVersionUID = -1718561876002831254L;
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String title;
	private String description;
	
	@Column(name="shared", columnDefinition="boolean default false", nullable=false)
	private boolean shared = SharedStatus.PRIVATE.status();

	@Enumerated(EnumType.STRING)
	@JsonProperty(value="status")
	@Column(name="read_later_status", nullable=false)
	private ReadLaterStatus readLaterStatus = ReadLaterStatus.NA;
	
	@ManyToOne(fetch=FetchType.EAGER, targetEntity=WebResource.class,
			cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="webresource_id")
	@JsonIgnore
	private WebResource webResource;
	
	@ManyToMany(targetEntity=Tag.class, fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name="bookmark_tags",
			joinColumns={@JoinColumn(name="bookmark_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	@OrderColumn(name="idx")
	@Size(max=5)
	private List<Tag> tags = new ArrayList<Tag>();
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="user_id")
	@JsonIgnore
	private User user;
	
	@Transient
	private int refCount;
	
	public Bookmark() {}
	
	public Bookmark(User user, WebResource webResource) {
		this.webResource = webResource;
		this.user = user;
		this.title = webResource.getTitle();
		this.description = webResource.getDescription();
	}
	
	public Bookmark(String url) {
		this.webResource = new WebResource(user, url);
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
	
	public String getUrl() {
		return (webResource == null ? null : webResource.getUrl());
	}
	public void setUrl(String url) {
		this.webResource = new WebResource(url);
	}
	
	public WebResource getWebPage() {
		return this.webResource;
	}
	public void setWebPage(WebResource webResource) {
		this.webResource = webResource;
	}
	
	public ReadLaterStatus getReadLaterStatus() {
		return readLaterStatus;
	}
	public void setReadLaterStatus(ReadLaterStatus readLaterStatus) {
		this.readLaterStatus = readLaterStatus;
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
			.add("webPage", webResource.getId())
			.add("shared", shared)
			.add("title", title)
			.add("tags", tags);
	}
	
}
