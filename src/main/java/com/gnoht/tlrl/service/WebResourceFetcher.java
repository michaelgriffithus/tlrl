package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;

public interface WebResourceFetcher<T extends WebResource<?, ?, T>> {

	public T fetch(T webResource);
	public T fetch(User user, String url);
}
