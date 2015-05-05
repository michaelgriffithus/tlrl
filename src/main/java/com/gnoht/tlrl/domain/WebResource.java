package com.gnoht.tlrl.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import com.gnoht.tlrl.domain.support.Managed;
import com.google.common.base.MoreObjects.ToStringHelper;

public abstract class WebResource<C,ID extends Serializable, T extends WebResource<C, ID, T>>
		extends Managed<ID> {

	private static final long serialVersionUID = -4700053364051467620L;

	public abstract String getUrl();
	public abstract void setUrl(String url);
	public abstract String getTitle();
	public abstract void setTitle(String title);
	public abstract String getDescription();
	public abstract void setDescription(String description);
	public abstract C getContent();
	public abstract void setContent(C content);
	public abstract Date getDateCreated();
	public abstract Date getDateModified();
	public abstract Set<Tag> getTags();
	public abstract void setTags(Set<Tag> tags);

	public abstract T update(T from);
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
			.add("url", getUrl())
			.add("title", getTitle())
			.add("dateCreated", getDateCreated())
			.add("dateModified", getDateModified());
	}

}
