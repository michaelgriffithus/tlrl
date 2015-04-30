package com.gnoht.tlrl.repository;

import java.util.Collections;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;

import com.gnoht.tlrl.domain.ReadLaterWebPage;
import com.gnoht.tlrl.domain.User;

public class ReadLaterWebPageSolrRepositoryImpl 
		implements ReadLaterWebPageCustomSolrRepository<ReadLaterWebPage> {

	private static final Logger LOG = LoggerFactory.getLogger(ReadLaterWebPageSolrRepositoryImpl.class);
	
	protected static final String PROP_KEY_SEARCH_PROJECTIONS = "tlrl.search.fieldsToProject";
	
	protected static final String FIELD_USERID = "userId";
	protected static final String FIELD_TAGS = "tags";
	protected static final String FIELD_SHARED = "shared";
	protected static final String FIELD_DEFAULT_SEARCH = "text";
	
	protected static final String DEFAULT_SEARCH_PROJECTIONS = "id,url,userId,userName,title,tags,shared,rlStatus,description,dateCreated";
	
	@Resource private SolrTemplate solrTemplate;
	@Resource private Environment env;
	
	private String[] searchProjections;
	
	@Override
	public SearchResultPage search(
				Set<String> terms, Set<String> tags, Pageable pageable) {
		FacetQuery query = buildBaseQueryWithFaceting(terms, pageable);
		addFilterQueryForTags(query, tags);
		addFilterQueryForShared(query, false);
		return doSearch(query);
	}
	
	@Override
	public SearchResultPage search(Set<String> terms, Set<String> tags,
			User user, boolean isOwner, Pageable pageable) {
		FacetQuery query = buildBaseQueryWithFaceting(terms, pageable);
		addFilterQueryForUser(query, user);
		addFilterQueryForShared(query, isOwner);
		addFilterQueryForTags(query, tags);
		return doSearch(query);
	}

	protected SearchResultPage doSearch(FacetQuery query) {
		return new SearchResultPage(solrTemplate.
				queryForFacetPage(query, ReadLaterWebPage.class));
	}

	/**
	 * Builds a FacetQuery to get tags associated with a search. Ideally we'd 
	 * do the search (with highlighting) and faceting in one step, but Spring 
	 * doesn't support highlighting+faceting in one query.
	 * 
	 * @param terms terms of the search
	 * @return 
	 */
	protected FacetQuery buildBaseQueryWithFaceting(Set<String> terms, Pageable pageable) {
		SimpleFacetQuery query = new SimpleFacetQuery(
				Criteria.where(FIELD_DEFAULT_SEARCH).contains(terms))
			.setPageRequest(pageable);
		
		query.addProjectionOnFields(getSearchProjections());
		
		// this is what we really want, just the tags that would have 
		// been faceted with the original search
		return query.setFacetOptions(new FacetOptions()
			.addFacetOnField(FIELD_TAGS)
			.setFacetMinCount(0));
	}
	
	protected Query addFilterQueryForShared(Query query, boolean isOwner) {
		if(!isOwner)  
			query.addFilterQuery(new SimpleFilterQuery(Criteria.where(FIELD_SHARED).is(true)));
		return query;
	}

	protected Query addFilterQueryForUser(Query query, User user) {
		if(user != null && user.getId() != null)
			query.addFilterQuery(new SimpleFilterQuery(Criteria.where(FIELD_USERID).is(user.getId())));
		return query;
	}
	
	/**
	 * Adds filter queries to main query to filter given tags. Filters 
	 * are added for each tag, creating an AND logic vs OR (e.g. tags 
	 * field contains ALL given tags in filter)
	 */
	protected Query addFilterQueryForTags(Query query, Set<String> tags) {
		if(!tags.isEmpty()) {
			for(String tag: tags) {
				query.addFilterQuery(new SimpleFilterQuery(Criteria.where(FIELD_TAGS).is(tag)));
			}
		}
		return query;
	}

	protected String[] getSearchProjections() {
		if(searchProjections == null) {
			searchProjections = env.getProperty(PROP_KEY_SEARCH_PROJECTIONS, 
				DEFAULT_SEARCH_PROJECTIONS).split(",");
		}
		return searchProjections;
	}
	
}
