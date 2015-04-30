package com.gnoht.tlrl.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.gnoht.tlrl.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={ Application.class })
@IntegrationTest
@WebAppConfiguration
public class WebPageServiceImplIntTests {

//	@Resource WebPageService webPageService;
//	@Resource WebPageMongoRepository webPageRepository;
//	@Resource ReadLaterWebPageSolrRepository readLaterWebPageSolrRepository;
//	
//	@Before
//	public void setUp() {
//		webPageRepository.deleteAll();
//		readLaterWebPageSolrRepository.deleteAll();
//		//webPageService.deleteAll();
//	}
	
}
