package com.gnoht.tlrl.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;
import com.gnoht.tlrl.security.CurrentUser;
import com.gnoht.tlrl.service.ReadLaterService;

@Controller
@RequestMapping(value="/bm")
public class BookmarkletController {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkletController.class);
	
	@Resource private ReadLaterService readLaterService;
	
	@RequestMapping(value="/add", method=RequestMethod.GET)
	public String addFromBookmarklet(@CurrentUser User currentUser, @RequestParam(required=true) String url, 
			@RequestParam(required=false) String title, RedirectAttributes redirectAttributes) {
		LOG.debug("Starting add(): user={}, url={}, title={}", currentUser, url, title);
		Bookmark bookmark = new Bookmark(currentUser, new WebPage(currentUser, url));
		bookmark = readLaterService.findOrCreateReadLater(bookmark);
		redirectAttributes.addFlashAttribute("readLater", bookmark);
		return "redirect:/bm/add/complete";
	}
	
	@RequestMapping(value="/add/complete", method=RequestMethod.GET) 
	public String addComplete(@CurrentUser User currentUser, Model model) {
		if(model.containsAttribute("readLater"))
			return "bookmarklet/addcomplete";
		else {
			return "redirect:/@" + currentUser.getName();
		}
	}

}
