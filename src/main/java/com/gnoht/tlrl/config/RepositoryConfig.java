package com.gnoht.tlrl.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.solr.client.solrj.SolrServer;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import com.gnoht.tlrl.domain.DomainPackage;
import com.gnoht.tlrl.repository.BookmarkListener;
import com.gnoht.tlrl.repository.BookmarkedResourceListener;
import com.gnoht.tlrl.repository.RepositoryPackage;
import com.gnoht.tlrl.service.BookmarkedResourceService;
import com.gnoht.tlrl.service.BookmarkedResourceServiceImpl;
import com.gnoht.tlrl.service.ReadLaterWebPageService;
import com.zaxxer.hikari.HikariDataSource;

/**
 * {@link Configuration} for repositories and related resources.
 */
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackageClasses={RepositoryPackage.class})
@EntityScan(basePackageClasses={DomainPackage.class})
public class RepositoryConfig {

	@Resource private Environment env;
	
	/**
	 * Creates {@link DataSource} based on "spring.datasource.*" properties, 
	 * which are source in depending on spring.profiles.active property. For
	 * each environment specific profile (e.g. dev, production), there are
	 * corresponding application-*.properties with appropriate datasource configs.
	 *  
	 * @return
	 * @throws Exception
	 */
	@Bean(name="dataSource", destroyMethod="close")
	public DataSource dataSource() throws Exception {
		HikariDataSource dataSource = new HikariDataSource();
		// see https://github.com/brettwooldridge/HikariCP: 
		// Spring Boot auto-configuration users, you need to use jdbcUrl-based configuration 
		dataSource.setDriverClassName(env.getRequiredProperty("spring.datasource.driverClassName"));
		dataSource.setUsername(env.getRequiredProperty("spring.datasource.username"));
		dataSource.setPassword(env.getRequiredProperty("spring.datasource.password"));
		dataSource.setJdbcUrl(env.getRequiredProperty("spring.datasource.url"));
		return dataSource;
	}
	
	@Bean
	@Autowired
	public SolrTemplate solrTemplate(SolrServer server) throws Exception {
		return new SolrTemplate(server);
	}
	
	@Configuration
	@Order(100)
	public static class EventListenerConfigurer {
		
		@Resource private EntityManagerFactory emf;
		@Resource private BookmarkedResourceService bookmarkedResourceService;
		@Resource private ReadLaterWebPageService readLaterWebPageService;
		
		@Bean
		public SessionFactory sessionFactory() {
			SessionFactory sessionFactory = emf.unwrap(SessionFactory.class);
			if(sessionFactory == null) {
				throw new NullPointerException("Factory is not a hibernate factory");
			}
			return sessionFactory;
		}

		@PostConstruct
		public void registerEntityListeners() {
			EventListenerRegistry registry = ((SessionFactoryImpl) sessionFactory())
					.getServiceRegistry().getService(EventListenerRegistry.class);
			
			registry.getEventListenerGroup(EventType.POST_COMMIT_INSERT)
				.appendListeners(new BookmarkListener(bookmarkedResourceService),
						new BookmarkedResourceListener(readLaterWebPageService));
		}
		
	}
}
