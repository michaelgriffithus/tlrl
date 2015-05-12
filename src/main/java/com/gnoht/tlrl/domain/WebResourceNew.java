package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

//@Entity(name="webresource")
//@Table(uniqueConstraints={
//	@UniqueConstraint(columnNames={"url"})	
//})
public class WebResourceNew extends ManagedAuditable<Long> {

	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition="text", unique=true, nullable=false, updatable=false)
	private String url;
	
	@Column(columnDefinition="text", nullable=true)
	private String description;
	
	@Column(columnDefinition="text", nullable=true)
	private String title;
	
	@Column(nullable=true) @Lob
	private byte[] content;
	
	@OneToMany(mappedBy="webResource", fetch=FetchType.LAZY,
			targetEntity=Bookmark.class,
			cascade={CascadeType.MERGE, CascadeType.PERSIST})
	private Set<Bookmark> bookmarks = new HashSet<Bookmark>();
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToMany(targetEntity=Tag.class, fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name="webresource_tags",
			joinColumns={@JoinColumn(name="webresource_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	@OrderColumn(name="idx")
	@Size(max=5)
	private Collection<Tag> tags = new ArrayList<Tag>();
	
	@Transient
	private int refCount;
	
	public WebResourceNew() {}
	
	private WebResourceNew(Long id, String url, String title, String description,
			byte[] content, Collection<Tag> tags, Set<Bookmark> bookmarks, User user) {
		this.id = id;
		this.url = url;
		this.title = title;
		this.description = description;
		this.content = content;
		this.tags = tags;
		this.bookmarks = bookmarks;
		this.user = user;
	}
	
	@Override
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
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

	public Collection<Tag> getTags() {
		return Collections.unmodifiableCollection(tags);
	}
	public void setTags(Collection<Tag> tags) {
		this.tags = tags;
	}
	
	public Collection<Bookmark> getBookmarks() {
		return Collections.unmodifiableSet(bookmarks);
	}
	public void setBookmarks(Set<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}

	public int getRefCount() {
		return refCount;
	}
	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	/**
	 * Returns a {@link Builder} for creating a new {@link WebResource}.
	 * @param url url referenced by this WebResource instance.
	 * @return an instance of Builder.
	 */
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Returns a {@link Builder} for creating a new {@link WebResource}.
	 * @return an instance of Builder.
	 */
	public static Builder builder(String url, User user) {
		return new Builder(url, user);
	}
	/**
	 * Simple builder implementation for creating and updating 
	 * a {@link WebResource}.
	 */
	public static class Builder {
		private Long id;
		private String title;
		private String description;
		private String url;
		private byte[] content;
		private Collection<Tag> tags = new ArrayList<>();
		private Set<Bookmark> bookmarks = new HashSet<>();
		private User user;
		
		public Builder() {}
		public Builder(String url, User user) {
			this();
			url(url);
			user(user);
		}
		
		public Builder id(Long id) {
			this.id = id; return this;
		}
		public Builder url(String url) {
			this.url = url; return this;
		}
		public Builder title(String title) {
			this.title = title; return this;
		}
		public Builder description(String desc) {
			this.description = desc; return this;
		}
		public Builder content(byte[] content) {
			this.content = content; return this;
		}
		public Builder user(User user) {
			this.user = user; return this;
		}
		public Builder tags(Collection<Tag> tags) {
			this.tags = tags; return this;
		}
		public Builder bookmark(Bookmark bookmark) {
			this.bookmarks.add(bookmark); return this;
		}
		public WebResourceNew build() {
			return new WebResourceNew(id, url, title, description, content, tags, bookmarks, user);
		}
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("url", url)
			.add("description", description)
			.add("title", title)
			.add("user", (user == null ? "" : user.getName()));
	}
}
