package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Class representing a saved URL of a web resource.
 */
@Entity(name="bookmark")
@Table(uniqueConstraints={
	/* each user may have 1 bookmark per url */
	@UniqueConstraint(columnNames={"webresource_id", "user_id"})	
})
public class Bookmark extends ManagedAuditable<Long> {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	/* http://www.depesz.com/2010/03/02/charx-vs-varcharx-vs-varchar-vs-text/ */
	@Column(columnDefinition="text", nullable=true, updatable=true)
	private String description;
	
	@Column(columnDefinition="text", nullable=true, updatable=true)
	private String title;
	
	@ManyToMany(targetEntity=Tag.class, fetch=FetchType.EAGER, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name="bookmark_tags",
			joinColumns={@JoinColumn(name="bookmark_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	@OrderColumn(name="idx")
	@Size(max=5) /* a bookmark can have at most 5 tags */
	private List<Tag> tags = new ArrayList<>();
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="user_id")
	@JsonIgnore
	private User user;
	
	@ManyToOne(fetch=FetchType.EAGER, targetEntity=WebResource.class,
			cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="webresource_id")
	@JsonIgnore
	private WebResource webResource;
	
	private Bookmark() {}
	
	/**
	 * Returns a {@link Builder} for creating a new {@link Bookmark}.
	 * @param webResource webResource referenced by this bookmark
	 * @return an instance of Builder.
	 */
	public static Builder builder(WebResource webResource) {
		return new Builder(webResource);
	}
	/**
	 * Returns a {@link Builder} for creating a new {@link Bookmark}.
	 * 
	 * @param url url of {@link WebResource} referenced by this url.
	 * @param user creator of the bookmark
	 * @return an instance of Builder
	 */
	public static Builder builder(String url, User user) {
		return new Builder(WebResource
				.builder(url, user)
				.get());
	}
	
	/**
	 * Returns a {@link Builder} for updating the passed in {@link Bookmark}.
	 * 
	 * @param bookmark to wrap {@link Builder} around.
	 * @return an instance of Builder.
	 */
	public static Builder updater(Bookmark bookmark) {
		return new Builder(bookmark);
	}
	
	@Override
	public Long getId() {
		return id;
	}
	public String getUrl() {
		return webResource.getUrl();
	}
	public String getDescription() {
		return description;
	}
	public String getTitle() {
		return title;
	}
	public List<Tag> getTags() {
		return Collections.unmodifiableList(tags);
	}
	@JsonIgnore
	public User getUser() {
		return user;
	}
	@JsonProperty
	public void setUser(User user) {
		this.user = user;
	}
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

	/**
	 * Simple implementation of builder pattern for creating
	 * and updating a Bookmark.
	 */
	public static class Builder {
		final Bookmark bookmark;
		
		private Builder(Bookmark bookmark) {
			this.bookmark = bookmark;
		}
		private Builder(WebResource webResource) {
			this.bookmark = new Bookmark();
			webResource(webResource);
		}
		public Builder webResource(WebResource webResource) {
			bookmark.webResource = webResource; return this;
		}
		public Builder id(Long id) {
			bookmark.id = id; return this;
		}
		public Builder title(String title) {
			bookmark.title = title; return this;
		}
		public Builder description(String description) {
			bookmark.description = description; return this;
		}
		public Builder tag(Tag tag) {
			bookmark.getTags().add(tag); return this;
		}
		public Builder tags(List<Tag> tags) {
			bookmark.tags = tags; return this;
		}
		public Builder user(User user) {
			bookmark.user = user; return this;
		}
		public Bookmark get() {
			return bookmark;
		}
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			//.add("url", url)
			.add("title", title)
			.add("description", description)
			.add("tags.size", (tags != null ? tags.size() : ""))
			.add("user", (user != null ? user.getName() : ""));
	}
}
