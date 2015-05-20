package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;

import com.gnoht.tlrl.domain.WebUrl;

public interface WebResourceFetcher {

	public WebUrl fetch(WebUrl webUrl);
	public WebUrl fetch(User user, String url);
}
