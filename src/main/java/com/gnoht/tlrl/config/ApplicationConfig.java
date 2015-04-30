package com.gnoht.tlrl.config;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.Environment;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Configuration
@EnableAsync
public class ApplicationConfig {
	
	@Resource private Environment env;

	@Bean(name = "jacksonMessageConverter")
	public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
		// converter.getObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
		// true);
		return converter;
	}

	@Bean
	public SolrServer solrServer() {
		return new HttpSolrServer(env.getRequiredProperty("spring.data.solr.host"));
	}

	@Bean
	public SolrTemplate solrTemplate(SolrServer server) throws Exception {
		return new SolrTemplate(server);
	}

	/**
	 * Returns a {@link MessageSourceAccessor} instance configured to use
	 * {@link LocaleContextHolder} for locale lookups. Using MessageSourceAccessor
	 * is more convenient then working with {@link MessageSource} directly.
	 * 
	 * @param messageSource
	 *          MessageSource being wrapped
	 * @return instance of MessageSourceAccessor
	 */
	@Autowired
	@Bean(name = "messageSourceAccessor")
	public MessageSourceAccessor messageSourceAccessor(MessageSource messageSource) {
		return new MessageSourceAccessor(messageSource);
	}
}
