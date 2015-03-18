package com.gnoht.tlrl.controller.support;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gnoht.tlrl.domain.support.Manageable;

/**
 * Response for valid request that wraps the returned data. 
 */
public class ResourceResponse 
		extends AbstractApiResponse<Object> {

	Object resource;
	
	public ResourceResponse(String uri, Page<?> page) {
		super(uri);
		this.resource = page.getContent();
	}
	
	public ResourceResponse(String uri, Manageable<?> resource) {
		super(uri);
		this.resource = resource;
	}
	
	@JsonProperty("data")
	@Override
	public Object getData() {
		return resource;
	}
}
