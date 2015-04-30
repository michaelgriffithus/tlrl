package com.gnoht.tlrl.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.gnoht.tlrl.Application;
//import com.gnoht.tlrl.domain.IndexedWebPage;
//import com.gnoht.tlrl.domain.WebResource;
//import com.gnoht.tlrl.repository.ReadLaterWebPageSolrRepository;
//import com.gnoht.tlrl.repository.WebPageMongoRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes= { Application.class })
@WebAppConfiguration
@IntegrationTest
public class WebResourceRepositoryIntTests {

//  @Resource private WebPageMongoRepository webResourceRepository;
//  @Resource private ReadLaterWebPageSolrRepository indexedWebResourceRepository;
//
//  @Before
//  public void setUp() {
//    webResourceRepository.deleteAll();
//    indexedWebResourceRepository.deleteAll();
//  }
//
//  @Test
//  public void testSavingWebResource() {
//    WebPage webResource = new 
//        WebPage("http://en.wikipedia.org/wiki/Craigslist");
//
//    // validate Mongo repository is empty
//    assertTrue("Repository is not empty", 
//        webResourceRepository.findAll().isEmpty());
//    // try saving WebResource    
//    webResourceRepository.save(webResource);
//    // verify WebResource was saved
//    assertNotNull("WebResource was not saved", 
//        webResourceRepository.findOne(webResource.getId()));
//  }
//
//  @Test
//  public void testIndexingWebResource() {
//    IndexedWebPage webResource = new IndexedWebPage();
//    webResource.setUrl("http://en.wikipedia.org/wiki/Craigslist");
//    webResource.setId("abc123");
//
//    // validate Solr repository is empty
//    assertTrue("Repository is not empty", 
//        indexedWebResourceRepository.count()==0);
//    // try saving WebResource    
//    //indexedWebResourceRepository.save(webResource);
//    // verify WebResource was saved
//    assertNotNull("WebResource was not saved", 
//        indexedWebResourceRepository.findOne(webResource.getId()));
//  }
}