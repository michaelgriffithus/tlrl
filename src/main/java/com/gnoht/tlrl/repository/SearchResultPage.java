package com.gnoht.tlrl.repository;

import static com.gnoht.tlrl.repository.ReadLaterWebPageSolrRepositoryImpl.FIELD_TAGS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.core.query.result.SolrResultPage;

import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.Tag;

public class SearchResultPage implements ResultPage<ReadLaterWebPage> {

	private Page<ReadLaterWebPage> page;
	private final List<ReadLaterWebPage> content;
	private List<Tag> allTags = new ArrayList<Tag>();
	private List<Tag> relatedTags = new ArrayList<Tag>();
	
//	public SearchResultPage(HighlightPage<ReadLaterWebPage> page, FacetPage<ReadLaterWebPage> tagsPage) {
//		checkIfSupportedPage(page);
//		this.page = page;
//		this.content = new ArrayList<ReadLaterWebPage>();
//		for(HighlightEntry<ReadLaterWebPage> hlEntry: page.getHighlighted()) {
//			ReadLaterWebPage webResource = hlEntry.getEntity();
//			try {
//				for(Highlight hl: hlEntry.getHighlights()) {
//					if(!hl.getField().getName().equals(FIELD_TAGS)) {
//							new Statement(webResource, createSetterStatement(hl.getField().getName()), 
//									new String[]{hl.getSnipplets().get(0)}).execute();
//					}
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			parseFacetedTags(tagsPage.getFacetResultPage(FIELD_TAGS));
//			content.add(webResource);
//		}
//	}

	public SearchResultPage(FacetPage<ReadLaterWebPage> page) {
		checkIfSupportedPage(page);
		this.page = page;
		this.content = page.getContent();
		parseFacetedTags(page.getFacetResultPage(FIELD_TAGS));
	}
	
	
	private String createSetterStatement(String field) {
		return "set" + (Character.toUpperCase(field.charAt(0)) + field.substring(1));
	}
	
	private void checkIfSupportedPage(Page<ReadLaterWebPage> page) {
		if(!(page instanceof SolrResultPage<?>)) 
			throw new IllegalArgumentException("Expected SolrResultPage, instead of: " + page.getClass());
	}
	
	private void parseFacetedTags(Page<FacetFieldEntry> page) {
		if(page.hasContent()) {
			List<FacetFieldEntry> facets = page.getContent();
			for(int i=0; i < facets.size(); i++) {
				FacetFieldEntry facet = facets.get(i);
				Tag tag = new Tag(facet.getValue(), (int) facet.getValueCount());
				allTags.add(tag);
				if(tag.getCount() > 0) {
					relatedTags.add(tag);
				}
			}
		}
	}
	
	@Override
	public final List<Tag> getAllTags() {
		return allTags;
	}
	@Override
	public final List<Tag> getRelatedTags() {
		return relatedTags;
	}

	@Override
	public final List<ReadLaterWebPage> getContent() {
		return content;
	}
	@Override
	public int getNumber() {
		return page.getNumber();
	}
	@Override
	public int getNumberOfElements() {
		return page.getNumberOfElements();
	}
	@Override
	public int getSize() {
		return page.getSize();
	}
	@Override
	public Sort getSort() {
		return page.getSort();
	}
	@Override
	public long getTotalElements() {
		return page.getTotalElements();
	}
	@Override
	public int getTotalPages() {
		return page.getTotalPages();
	}
}
