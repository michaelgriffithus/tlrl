package com.gnoht.tlrl.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.*;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Abstract test class for standalone controller testing. Provides basic
 * infrastructure support for wiring up a standalone test.
 * 
 * @param <T> type of target controller
 */
public abstract class StandaloneControllerTest<T> {

	protected MockMvc mockMvc;
	protected HttpMessageConverter messageConverter = defaultMessageConverter();
	protected MediaType contentType = defaultMediaType();

	@InjectMocks
	protected T controller = createController();

	/**
	 * Implementing test classes are responsible for providing target 
	 * controller instance to be tested.
	 * @return
	 */
	protected abstract T createController();
	
	@Before
	public void initMocks() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setMessageConverters(messageConverter)
				.alwaysDo(print())
				.alwaysExpect(status().isOk())
				.alwaysExpect(content().contentType(contentType))
			.build();
	}

	protected MappingJackson2HttpMessageConverter defaultMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.getObjectMapper().setSerializationInclusion(Include.NON_NULL);
		return converter;
	}
	
	/**
	 * Helper for converting test object data to JSON string.
	 * @param o
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected String toJson(Object o) throws IOException {
    MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
    messageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
    return mockHttpOutputMessage.getBodyAsString();
	}
	
	/**
	 * Helper for converting JSON string to object.
	 * @param objClass
	 * @param contents
	 * @return
	 * @throws HttpMessageNotReadableException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected Object fromJson(Class<?> objClass, byte[] contents) 
			throws HttpMessageNotReadableException, IOException {
		return messageConverter.read(objClass, new MockHttpInputMessage(contents));
	}
	
	protected MediaType defaultMediaType() {
		return new MediaType(APPLICATION_JSON.getType(), APPLICATION_JSON.getSubtype(), Charset.forName("UTF-8"));
	}
}
