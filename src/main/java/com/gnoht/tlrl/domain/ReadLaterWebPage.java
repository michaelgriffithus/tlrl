package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty(value="status")
	private ReadLaterStatus readLaterStatus;
	
	@Field @Indexed
	private String userName;
	
	@Field(value="tags") @Indexed(required=false)
	private List<String> indexedTags = new ArrayList<String>();
	
	@Field
	private Date dateCreated;
	private Date dateModified;
	
	public ReadLaterWebPage() {}
	
	public ReadLaterWebPage(ReadLater readLater) {
		this.id = readLater.getId().toString();
		this.dateCreated = readLater.getDateCreated();
		this.dateModified = readLater.getDateModified();
		this.description = readLater.getDescription();
		this.setTags(readLater.getTags());
		this.shared = readLater.isShared();
		this.readLaterStatus = readLater.getReadLaterStatus();
		this.title = readLater.getTitle();
		this.url = readLater.getUrl();
		this.userName = readLater.getUserName();
		this.userId = readLater.getUserId();
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
	public ReadLaterStatus getReadLaterStatus() {
		return readLaterStatus;
	}
	public void setReadLaterStatus(ReadLaterStatus readLaterStatus) {
		this.readLaterStatus = readLaterStatus;
	}

	@Deprecated
	public ReadLaterWebPage update(ReadLaterWebPage from) {
		this.dateModified = new Date();
		this.description = from.getDescription();
		this.title = from.getTitle();
		this.shared = from.isShared();
		this.readLaterStatus = from.readLaterStatus;
		this.setTags(from.getTags());
		return this;
	}
}
