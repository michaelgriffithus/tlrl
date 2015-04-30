package com.gnoht.tlrl.domain;

import java.util.Random;

public class DomainTestUtils {

	public static final String[] TEST_URLS = {
		"https://web.archive.org/web/20140104014205/http://en.wikipedia.org/wiki/Craigslist",
		"https://web.archive.org/web/20140101002948/http://stackoverflow.com/",
		"https://web.archive.org/web/20140705115946/http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/"
	};

	private static Random random = new Random();
	
	public static WebPage newWebPage() {
		return new WebPage(null, TEST_URLS[random.nextInt(TEST_URLS.length)]);
	}
	
	public static WebPage getWebPage() {
		WebPage webPage = newWebPage();
//		webPage.setId(createIdAsString());
		return webPage;
	}
	
	public static WebPage[] newWebPage(String ... urls) {
		WebPage[] webPages = new WebPage[urls.length];
		for(int i=0; i < urls.length; i++) 
			webPages[i] = new WebPage(null, urls[i]);
		return webPages;
	}

//	public static WebPage[] getWebPages(String ... urls) {
//		WebPage[] webPages = newWebPage(urls);
//		for(WebPage webPage: webPages) 
////			webPage.setId(createIdAsString());
//		return webPages;
//	}
	
	public static String createIdAsString() {
		//return new ObjectId().toHexString();
		return null;
	}
}
