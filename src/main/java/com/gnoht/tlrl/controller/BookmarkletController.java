package com.gnoht.tlrl.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.BookmarkService;

@Controller
@RequestMapping(value="/bm")
public class BookmarkletController {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkletController.class);
	
	@Resource private BookmarkService bookmarkService;
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addFromBookmarklet(@CurrentUser User currentUser, 
				Bookmark bookmark, RedirectAttributes redirectAttributes) {
		LOG.debug("Starting add(): user={}, bookmark={}", currentUser, bookmark);
		bookmark.setUser(currentUser);
		bookmark = bookmarkService.findOrCreate(bookmark);
		redirectAttributes.addFlashAttribute("bookmark", bookmark);
		return "redirect:/bm/add/complete";
	}
	
	@RequestMapping(value="/add/complete", method=RequestMethod.GET) 
	public String addComplete(@CurrentUser User currentUser, Model model) {
		if(model.containsAttribute("bookmark"))
			return "bookmarklet/addcomplete";
		else {
			return "redirect:/@" + currentUser.getName();
		}
	}

}
