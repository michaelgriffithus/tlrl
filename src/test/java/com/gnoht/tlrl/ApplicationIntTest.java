package com.gnoht.tlrl;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.gnoht.tlrl.config.RepositoryConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={RepositoryConfig.class})
@ActiveProfiles(profiles={"test"})
public class ApplicationIntTest {

	@Resource
	private DataSource dataSource;
	
	@Test
	public void dataSourceShouldNotBeNull() {
		assertNotNull(dataSource);
	}
}
