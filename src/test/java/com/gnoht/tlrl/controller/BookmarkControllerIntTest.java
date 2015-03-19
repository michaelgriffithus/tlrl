package com.gnoht.tlrl.controller;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gnoht.tlrl.Application;
import com.gnoht.tlrl.service.BookmarkService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={Application.class})
@WebAppConfiguration
public class BookmarkControllerIntTest {

	MockMvc mockMvc;
	
	@Autowired
	WebApplicationContext webApplicationContext;
	
	@Mock
	BookmarkService bookmarkService;
	
	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders
			.webAppContextSetup(webApplicationContext)
				.build();
	}
	
	@Test
	public void notFound() throws Exception {
		mockMvc.perform(get("/"))
			.andDo(print());
	}
}
