package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;

import com.gnoht.tlrl.domain.WebResource;

public interface WebResourceFetcher {

	public WebResource fetch(WebResource webResource);
	public WebResource fetch(User user, String url);
}
