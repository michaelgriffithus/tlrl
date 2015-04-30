package com.gnoht.tlrl.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Utility for building {@link WebURL}s during testing. 
 */
public class WebResourceBuilder {
	
	static final String[] TEST_URLS = {
		"https://web.archive.org/web/20140104014205/http://en.wikipedia.org/wiki/Craigslist",
		"https://web.archive.org/web/20140101002948/http://stackoverflow.com/",
		"https://web.archive.org/web/20140705115946/http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/"
	};
	
	static final Random random = new Random();

	public static WebResource buildResource(boolean withId) {
		int index = random.nextInt(3);
		return build(TEST_URLS[index], withId ? Integer.toString(index) : null);
	}
	
	private static WebResource build(String url, String id) {
		WebResource webResource = new WebPage(null, url);
		if(id != null) {
			webResource.setId(id);
		}
		return webResource;
	}

	public static List<WebResource> buildURLs(boolean withId) {
		List<WebResource> webURLs = new ArrayList<WebResource>();
		for(int i=0; i < TEST_URLS.length; i++) {
			webURLs.add(build(TEST_URLS[i], withId ? Integer.toString(i) : null));
		}
		return webURLs;
	}
}