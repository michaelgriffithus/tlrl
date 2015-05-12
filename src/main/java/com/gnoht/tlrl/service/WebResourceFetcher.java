package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;

import com.gnoht.tlrl.domain.WebPage;

public interface WebResourceFetcher {

	public WebPage fetch(WebPage webPage);
	public WebPage fetch(User user, String url);
}
