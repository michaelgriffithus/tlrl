package com.gnoht.tlrl.repository.readlater;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.gnoht.tlrl.controller.ReadLaterQueryFilter;
import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.SharedStatus;

/**
 * {@link SqlParameterSource} implementation holding named SQL parameters 
 * for all "findAll*" queries.
 */
public class ReadLaterSqlParameterSource 
		extends MapSqlParameterSource {

	/** Flag indicating query filter untagged is applied */
	static final String UNTAGGED_PARAM = "untagged";
	/** Flag indicating query filter with tags */
	static final String HAS_TAGS_PARAM = "hasTags";
	/** Flag indicating query filter with tags, and number of tags */
	static final String TAG_COUNT_PARAM = "tagCount";
	static final String READLATER_STATUS_PARAM = "readLaterStatus";
	static final String SHARED_STATUS_PARAM = "shared";
	static final String USER_ID_PARAM = "userId";
	static final String PAGE_PARAM = "page";
	static final String PAGE_SIZE_PARAM = "pageSize";
	
	
	static final Set<String> NULL_TAGS = null;
	static final Long NULL_USERID = null;  
	static final ReadLaterQueryFilter NULL_QUERYFILTER = null;
	
	private ReadLaterSqlParameterSource(Pageable pageable) {
		addValue(PAGE_PARAM, pageable.getOffset());
		addValue(PAGE_SIZE_PARAM, pageable.getPageSize());
	}

	@Override
	public ReadLaterSqlParameterSource addValue(String paramName, Object value) {
		super.addValue(paramName, value);
		return this;
	}

	/**
	 * Constructs {@link SqlParameterSource} for findAllByOwnerTagged query. 
	 * 
	 * @param userId
	 * @param tags
	 * @param filter
	 * @param pageable
	 */
	private ReadLaterSqlParameterSource(
			final Long userId, 
			final Set<String> tags, 
			final ReadLaterQueryFilter filter, 
			final Pageable pageable) 
	{
		this(pageable);
		addValue(UNTAGGED_PARAM, tags == null);
		addValue(HAS_TAGS_PARAM, false);
		if(tags != null) {
			addValue(HAS_TAGS_PARAM, !tags.isEmpty());
			addValue(TAG_COUNT_PARAM, tags.size());
			if(!tags.isEmpty()) {
				int tagCount = 0;
				for(String tag: tags) {
					// add each tag as a parameter with name of "tag" + index
					// e.g tag0, tag1, ...
					addValue(("tag" + tagCount++), tag);
				}
			}
		}
		
		if(userId != null) 
			addValue(USER_ID_PARAM, userId);
		
		if(filter != null) {
			if(filter.getReadLaterStatus() == ReadLaterStatus.UNREAD) {
				addValue(READLATER_STATUS_PARAM, ReadLaterStatus.UNREAD);
			}
			
			if(filter.getSharedStatus() == SharedStatus.PUBLIC)
				addValue(SHARED_STATUS_PARAM, true);
			else if(filter.getSharedStatus() == SharedStatus.PRIVATE)
				addValue(SHARED_STATUS_PARAM, false);
		}
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for all pageable queries.
	 *  
	 * @param pageable
	 * @return
	 */
	public static ReadLaterSqlParameterSource forPageableQueries(final Pageable pageable) {
		return new ReadLaterSqlParameterSource(pageable);
	}

	/**
	 * Constructs {@link SqlParameterSource} for *ByTags queries.
	 * 
	 * @param tags
	 * @param pageable
	 * @return
	 */
	public static ReadLaterSqlParameterSource forByTagsQueries(
			final Set<String> tags, 
			final Pageable pageable) 
	{
		return new ReadLaterSqlParameterSource(NULL_USERID, tags, NULL_QUERYFILTER, pageable);
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for findAllByUserAndTags query.
	 * 
	 * @param userId
	 * @param tags
	 * @param pageable
	 */
	public static ReadLaterSqlParameterSource forByUserAndTagsQueries(
			final Long userId, 
			final Set<String> tags, 
			final Pageable pageable) 
	{
		return new ReadLaterSqlParameterSource(userId, tags, NULL_QUERYFILTER, pageable);
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for *ByOwnerAndTaggedQueries.
	 * 
	 * @param userId
	 * @param tags
	 * @param queryFilter
	 * @param pageable
	 * @return
	 */
	public static ReadLaterSqlParameterSource forByOwnerAndTaggedQueries(
			final Long userId,
			final Set<String> tags,
			final ReadLaterQueryFilter queryFilter,
			final Pageable pageable) 
	{
		return new ReadLaterSqlParameterSource(userId, tags, queryFilter, pageable);
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for *ByOwnerUntagged queries.
	 * 
	 * @param userId
	 * @param filter
	 * @param pageable
	 * @return
	 */
	public static ReadLaterSqlParameterSource forByOwnerUntaggedQueries(
			final Long userId,
			final ReadLaterQueryFilter filter,
			final Pageable pageable) 
	{
		return new ReadLaterSqlParameterSource(userId, NULL_TAGS, filter, pageable);
	}
	
}
