package com.gnoht.tlrl.service;

import java.io.IOException;

import javax.annotation.Resource;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;

@Service("webPageFetcher")
public class WebPageFetcher implements WebResourceFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(WebPageFetcher.class);
	
	@Resource private WebUrlService webUrlService;
	@Resource private ReadLaterWebPageService readLaterWebPageService;
	
	//@Async
	@Override
	public WebUrl fetch(WebUrl webUrl) {
		LOG.info("Starting fetch(): {}", webUrl);
		try {
			Response response = Jsoup.connect(webUrl.getUrl())
					.method(Method.GET)
					.timeout(10000) //10secs
					.userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/32.0.1664.3 Safari/537.36")
					.maxBodySize(1500000) // 1.5MB ~ average page size in 2014
					.execute();
		
			// parses response for title and text
			Document document;
			document = response.parse();
			webUrl.setTitle(document.title());
			String content = Jsoup.clean(document.body().text(), Whitelist.basic());
			//webResource.setContent(content.getBytes());
			// TODO: parse for meta description
			webUrl.setDescription(content == null || content.length() < 200 ? 
					content : content.substring(0,  200));
			//webResource.setFetched(true);
		} catch (IOException e) {
			LOG.error("Unable to parse {}", webUrl.getUrl(), e);
			//webResource.setFetched(false);
		}
		webUrlService.save(webUrl);
		return webUrl;
	}

	@Override
	public WebUrl fetch(User user, String url) {
		WebUrl webUrl = new WebUrl(user, url);
		fetch(webUrl);
		return webUrl;
	}

}