package com.gnoht.tlrl.controller;

//import com.gnoht.tlrl.service.WebResourceService;

public class WebResourceControllerTests {

//	@Mock private WebPageService webPageService;
//	@InjectMocks private WebPageController webPageController;
//	
//	private MockMvc mockMvc;
//	
//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//		mockMvc = MockMvcBuilders.standaloneSetup(webPageController)
//				.setHandlerExceptionResolvers(createExceptionResolver())
//				.setCustomArgumentResolvers(handlerMethodArgumentResolver(), 
//						pageableMethodArgumentResolver() 
//					//	,pagedResourcesAssemblerArgumentResolver()
//					)
//				.setMessageConverters(messageConverters())
//				.build();
//	}
//	
//	private HttpMessageConverter<?> messageConverters() {
//		return new MappingJackson2HttpMessageConverter();
//	}
//
//	protected MessageSource messageSource() {
//		ResourceBundleMessageSource messageSource =
//				new ResourceBundleMessageSource();
//		
//		messageSource.setBasename("i18n/messages");
//		messageSource.setUseCodeAsDefaultMessage(true);
//		
//		return messageSource;
//	}
//	
////	private PagedResourcesAssemblerArgumentResolver pagedResourcesAssemblerArgumentResolver() {
////		return new PagedResourcesAssemblerArgumentResolver(new HateoasPageableHandlerMethodArgumentResolver(), null);
////	}
//
//	protected HandlerMethodArgumentResolver pageableMethodArgumentResolver() {
//		return new PageableHandlerMethodArgumentResolver();
//	}
//	
//	private HandlerMethodArgumentResolver handlerMethodArgumentResolver() {
//		return new HttpEntityMethodProcessor(Arrays.asList(new HttpMessageConverter<?>[]{messageConverters()}));
//	}
//	
//	private ExceptionHandlerExceptionResolver createExceptionResolver() {
//
//		ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
//			protected ServletInvocableHandlerMethod getExceptionHandlerMethod(
//					HandlerMethod handlerMethod, Exception exception) {
//				
//				Method method = new ExceptionHandlerMethodResolver(
//						DefaultExceptionHandler.class).resolveMethod(exception);
//				
//				DefaultExceptionHandler exceptionHandler = new DefaultExceptionHandler();
//				exceptionHandler.setMessageSource(new MessageSourceAccessor(messageSource(), LocaleContextHolder.getLocale()));
//				return new ServletInvocableHandlerMethod(exceptionHandler,method);
//			}
//		};
//		exceptionResolver.afterPropertiesSet();
//		return exceptionResolver;
//	}
	
//  when running as junit test vs int test, we get error:
//  org.springframework.web.HttpMediaTypeNotAcceptableException: Could not find acceptable representation
//	
//	@Test
//	public void testInvalidCreate() throws Exception {
//		final String webResourceID = "54624dac3004001225a5b1ba"; 
//		WebResource webResource = new WebResource();
//		when(webResourceService.create(webResource)).then(new Answer<WebResource>() {
//			@Override
//			public WebResource answer(InvocationOnMock invocation) throws Throwable {
//				WebResource webResource = (WebResource) invocation.getArguments()[0];
//				WebResource created = new WebResource(webResource.getUrl());
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
	

//	@Test
//	public void testCreate() throws Exception {
//		// given test WebURL data
//		final String idString = DomainTestUtils.createIdAsString(); 
//		final WebPage webPage = DomainTestUtils.newWebPage();
//		
//		// mock the webResourceService
//		when(webPageService.create(webPage)).then(new Answer<WebPage>() {
//			@Override
//			public WebPage answer(InvocationOnMock invocation) throws Throwable {
//				WebPage webPage = (WebPage) invocation.getArguments()[0];
//				WebPage created = new WebPage(webPage.getUrl());
//				created.setId(idString);
//				return created;
//			}
//		});
//		
//		// when controller gets POST with test webResource data 
//		mockMvc.perform(post("/api/urls")
//					.contentType(MediaType.APPLICATION_JSON)
//					.content(TestUtils.toJSON(webPage)))
//			// then expect the following response
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.id", is(idString)))
//			.andExpect(jsonPath("$.url", is(webPage.getUrl())));
//	
//		// also verify that webResourceService was called 
//		verify(webPageService, times(1)).create(webPage);
//		verifyNoMoreInteractions(webPageService);
//	}
//
//	
//	@Test
//	public void testUpdate() throws Exception {
//		WebPage webPage = DomainTestUtils.getWebPage();
//		when(webPageService.update(webPage)).thenReturn(webPage);
//
//		mockMvc.perform(put("/api/urls/" + webPage.getId())
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(TestUtils.toJSON(webPage)))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.id", is(webPage.getId())));
//		
//		verify(webPageService, times(1)).update(webPage);
//		verifyNoMoreInteractions(webPageService);
//	}
//	
//	@Test
//	public void testDelete() throws Exception {
//		WebPage webPage = DomainTestUtils.getWebPage();
//		when(webPageService.delete(anyString())).thenReturn(webPage);
//		
//		mockMvc.perform(delete("/api/urls/" + webPage.getId()))
//			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.id", is(webPage.getId())))
//			.andExpect(jsonPath("$.url", is(webPage.getUrl())));
//		
//		verify(webPageService, times(1)).delete(anyString());
//		verifyNoMoreInteractions(webPageService);
//	}
//	
//	@Test
//	public void testFindAll() throws Exception {
//		List<WebPage> webPages = Arrays.asList(DomainTestUtils.getWebPages(DomainTestUtils.TEST_URLS));
//		
//		Pageable pageable = new PageRequest(0, 3);
//		when(webPageService.findAllByUserIdAndTags(null, Collections.EMPTY_SET, true, pageable))
//			.thenReturn((new PageImpl<WebPage>(webPages, pageable, webPages.size())));
//		
//		mockMvc.perform(get("/api/urls"))
//			.andExpect(status().isOk())
//			.andDo(print())
//			.andExpect(jsonPath("$content", hasSize(webPages.size())))
//			.andExpect(jsonPath("$content[0].id", is(webPages.get(0).getId())))
//			.andExpect(jsonPath("$content[0].url", is(webPages.get(0).getUrl())));
//		
//		verify(webPageService, times(1)).findAllByUserIdAndTags(null, Collections.EMPTY_SET, true, pageable);
//		
//	}
}