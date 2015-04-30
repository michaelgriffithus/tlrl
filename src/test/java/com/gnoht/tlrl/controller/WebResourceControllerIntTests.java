package com.gnoht.tlrl.controller;

import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.gnoht.tlrl.Application;
//import com.gnoht.tlrl.service.WebResourceService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=Application.class)
@IntegrationTest
@WebAppConfiguration
public class WebResourceControllerIntTests {

//	@Resource EmbeddedWebApplicationContext webAppContext;
//	@Resource WebResourceController webResourceController;
//	
//	private WebResourceService webResourceService = mock(WebResourceService.class);
//	
//	MockMvc mockMvc;
//	
//	@Before
//	public void setUp() {
//		mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
//		webResourceController.setWebResourceService(webResourceService);
//	}
//	
//	@Test
//	public void testInvalidCreate() throws Exception {
//		final String webResourceID = "54624dac3004001225a5b1ba"; 
//		WebResource webResource = new WebPage();
//		when(webResourceService.create(webResource)).then(new Answer<WebResource>() {
//			@Override
//			public WebResource answer(InvocationOnMock invocation) throws Throwable {
//				WebResource webResource = (WebResource) invocation.getArguments()[0];
//				WebResource created = new WebPage(webResource.getUrl());
//				created.setId(webResourceID);
//				created.setDescription(webResource.getDescription());
//				return created;
//			}
//		});
//		
//		mockMvc.perform(post("/api/urls")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtils.toJSON(webResource)))
//			.andDo(print())
//			.andExpect(status().is4xxClientError());
//	}
//	
}
