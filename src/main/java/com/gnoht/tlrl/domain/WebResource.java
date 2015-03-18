package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.gnoht.tlrl.domain.support.ManagedAuditable;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * Class representing a web page.
 */
@Entity(name="webresource")
@Table(uniqueConstraints={
	@UniqueConstraint(columnNames={"url"})	
})
public class WebResource extends ManagedAuditable<Long> {

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
	
	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.MERGE}, optional=false)
	@JoinColumn(name="user_id")
	private User user;
	
	@ManyToMany(targetEntity=Tag.class, fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name="webpage_tags",
			joinColumns={@JoinColumn(name="webpage_id")},
			inverseJoinColumns={@JoinColumn(name="tag_id")})
	@OrderColumn(name="idx")
	@Size(max=5)
	private Collection<Tag> tags = new ArrayList<Tag>();
	
	@Override
	public Long getId() {
		return id;
	}
	public String getUrl() {
		return url;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public Collection<Tag> getTags() {
		return Collections.unmodifiableCollection(tags);
	}
	public User getUser() {
		return user;
	}
	public byte[] getContent() {
		return content;
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
	 * Returns a {@link Builder} for updating a {@link WebResource}.
	 * @param webResource webResource to wrap.
	 * @return an instance of Builder.
	 */
	public static Builder updater(WebResource webResource) {
		return new Builder(webResource);
	}
	
	/**
	 * Simple builder implementation for creating and updating 
	 * a {@link WebResource}.
	 */
	public static class Builder {
		final WebResource webResource;
		
		public Builder() {
			webResource = new WebResource();
		}
		public Builder(WebResource webResource) {
			this.webResource = webResource;
		}
		public Builder(String url, User user) {
			this();
			url(url);
			user(user);
		}
		public Builder id(Long id) {
			webResource.id = id; return this;
		}
		public Builder url(String url) {
			webResource.url = url; return this;
		}
		public Builder title(String title) {
			webResource.title = title; return this;
		}
		public Builder description(String desc) {
			webResource.description = desc; return this;
		}
		public Builder content(byte[] content) {
			webResource.content = content; return this;
		}
		public Builder user(User user) {
			webResource.user = user; return this;
		}
		public Builder tags(Collection<Tag> tags) {
			webResource.tags = tags; return this;
		}
		public WebResource get() {
			return webResource;
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
