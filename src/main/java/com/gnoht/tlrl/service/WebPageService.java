package com.gnoht.tlrl.service;

import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;

public interface WebPageService extends ManageableService<Long, WebPage> {

	public WebPage findOrCreate(User user, String url);
	public WebPage findOrCreate(WebPage webPage);
	public WebPage findByUrl(String url);
	
//	/**
//	 * 
//	 * @param userId
//	 * @param tags
//	 * @param isPartial
//	 * @param pageable
//	 * @return
//	 */
//	public Page<WebPage> findAllByUserIdAndTags(
//			String userId, Set<String> tags, boolean isPartial, Pageable pageable);
//
//	/**
//	 * Create WebPage based on given url.
//	 * @param url to base new WebPage on
//	 * @return newly created WebPage
//	 */
//	public WebPage create(String url);
//	
//	/**
//	 * @see ManageableService#update(com.gnoht.tlrl.domain.Manageable), with option
//	 * to return partial or full representation of updated WebPage.
//	 * 
//	 * @param updated
//	 * @param returnPartial
//	 * @return
//	 * @throws ManageableNotFoundException
//	 */
//	public WebPage update(WebPage updated, boolean returnPartial) throws ManageableNotFoundException;
//	
//	/**
//	 * 
//	 * @param fetched
//	 * @return
//	 * @throws ManageableNotFoundException
//	 */
//	public WebPage setFetchedContent(WebPage fetched) throws ManageableNotFoundException;
//	
//	/**
//	 * 
//	 * @param id
//	 * @param isPartial
//	 * @return
//	 */
//	public WebPage findById(String id, boolean isPartial);
//	
//	/**
//	 * 
//	 * @param userId
//	 * @return
//	 */
//	public List<Tag> findAllTagsByUserId(String userId);
//	
//	/**
//	 * 
//	 * @param userId
//	 * @param tags
//	 * @return
//	 */
//	public List<Tag> findAllTagsByUserIdAndTags(String userId, Set<String> tags);
}
