package com.gnoht.tlrl.config;

import javax.sql.DataSource;

public interface DataSourceConfig {

	public DataSource dataSource() throws Exception;
}
