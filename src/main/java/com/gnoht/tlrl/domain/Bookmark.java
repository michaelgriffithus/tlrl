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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Class representing a saved URL of a web resource.
 */
@Entity(name="bookmark")
@Table(uniqueConstraints={
	/* each user may have 1 bookmark per url */
	@UniqueConstraint(columnNames={"url", "user_id"})	
})
public class Bookmark extends ManagedAuditable<Long> {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	/* http://www.depesz.com/2010/03/02/charx-vs-varcharx-vs-varchar-vs-text/ */
	@Column(columnDefinition="text", nullable=false, updatable=false)
	private String url;
	
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
	private User user;
	
	private Bookmark() {}
	
	/**
	 * Returns a {@link Builder} for creating a new {@link Bookmark}.
	 * @param url url referenced by this Bookmark instance.
	 * @return an instance of Builder.
	 */
	public static Builder builder(String url) {
		return new Builder(url);
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
		return url;
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
	public User getUser() {
		return user;
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
		private Builder(String url) {
			this.bookmark = new Bookmark();
			url(url);
		}
		public Builder url(String url) {
			bookmark.url = url; return this;
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
			.add("url", url)
			.add("title", title)
			.add("description", description)
			.add("tags.size", (tags != null ? tags.size() : ""))
			.add("user", (user != null ? user.getName() : ""));
	}
}
