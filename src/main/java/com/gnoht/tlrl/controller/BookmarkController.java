package com.gnoht.tlrl.controller;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gnoht.tlrl.controller.support.ResourceResponse;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.service.BookmarkService;

@RestController
@RequestMapping(value={"/api"})
public class BookmarkController {

	@Resource
	private BookmarkService bookmarkService;
	
	@RequestMapping(value={"/urls"}, method=RequestMethod.GET)
	public @ResponseBody Page<Bookmark> list(HttpServletRequest request) {
		return bookmarkService.findAll(new PageRequest(0, 10));
	}
	
	@RequestMapping(value={"/urls"}, method=RequestMethod.POST)
	public @ResponseBody Bookmark add(@Valid @RequestBody Bookmark bookmark) {
		System.out.println("============== in add:" + bookmark);
		return bookmarkService.create(bookmark);
	}
}
