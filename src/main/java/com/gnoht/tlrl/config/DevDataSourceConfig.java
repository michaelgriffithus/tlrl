package com.gnoht.tlrl.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration 
//@Profile("default")
public class DevDataSourceConfig implements DataSourceConfig {

	private static final Logger LOG =
			LoggerFactory.getLogger(DevDataSourceConfig.class);
	
	@Resource private Environment env; 

	@Bean(name="dataSource", destroyMethod="close")
	@Override
	public DataSource dataSource() throws Exception {
		
		String jdbcUrl = env.getRequiredProperty("spring.datasource.url");
		LOG.info("Configuring dev DataSource at: {}", jdbcUrl);
		BoneCPDataSource dataSource = new BoneCPDataSource();
		dataSource.setDriverClass(env.getRequiredProperty("spring.datasource.driverClassName"));
		dataSource.setUsername(env.getRequiredProperty("spring.datasource.username"));
		dataSource.setPassword(env.getRequiredProperty("spring.datasource.password"));
		dataSource.setJdbcUrl(jdbcUrl);
		return dataSource;
	}
}

