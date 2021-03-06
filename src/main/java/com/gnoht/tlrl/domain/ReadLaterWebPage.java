package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.domain.Bookmark.Status;
import com.gnoht.tlrl.domain.support.Managed;

@SolrDocument
public class ReadLaterWebPage extends Managed<String>{

	private static final long serialVersionUID = 2870120910769823172L;

	@Field @Id @Indexed
	private String id;
	
	@Field @Indexed(required=false)
	private String description;
	
	@Field @Indexed(required=false)
	private String title;

	@Field @Indexed(required=true)
	private boolean shared;
	
	@Field
	private String url;
	
	@Field @Indexed(required=false)
	private String content;
	
	@Field @Indexed
	private Long userId;
	
	@Field(value="rlStatus") @Indexed(required=false)
	private Status status;
	
	@Field @Indexed
	private String userName;
	
	@Field(value="tags") @Indexed(required=false)
	private List<String> indexedTags = new ArrayList<String>();
	
	@Field
	private Date dateCreated;
	private Date dateModified;
	
	public ReadLaterWebPage() {}
	
	public ReadLaterWebPage(Bookmark bookmark) {
		this.id = bookmark.getId().toString();
		this.dateCreated = bookmark.getDateCreated();
		this.dateModified = bookmark.getDateModified();
		this.description = bookmark.getDescription();
		this.setTags(bookmark.getTags());
		this.shared = bookmark.isShared();
		this.status = bookmark.getStatus();
		this.title = bookmark.getTitle();
		this.url = bookmark.getUrl();
		this.userName = bookmark.getUserName();
		this.userId = bookmark.getUserId();
	}
	
	@Override
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns {@link Tag}s associated with this ReadLaterResource. Tags are
	 * elevated from simple String representation.
	 * @return
	 */
	public List<Tag> getTags() {
		List<Tag> tags = new ArrayList<Tag>();
		for(String tagId: indexedTags) 
			tags.add(new Tag(tagId));
		return tags;
	}
	
	/**
	 * Set the {@link Tag}s associated with this ReadLaterResource. Tags are
	 * flattened from {@link Tag} to simple String.
	 * @param tags
	 */
	public void setTags(List<Tag> tags) {
		this.indexedTags = new ArrayList<String>();
		for(Tag tag: tags) {
			indexedTags.add(tag.getId());
		}
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
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}

	@Deprecated
	public ReadLaterWebPage update(ReadLaterWebPage from) {
		this.dateModified = new Date();
		this.description = from.getDescription();
		this.title = from.getTitle();
		this.shared = from.isShared();
		this.status = from.getStatus();
		this.setTags(from.getTags());
		return this;
	}
}
