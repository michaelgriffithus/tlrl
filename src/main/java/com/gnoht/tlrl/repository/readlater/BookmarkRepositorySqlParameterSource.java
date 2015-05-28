package com.gnoht.tlrl.repository.readlater;

import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.gnoht.tlrl.controller.ReadLaterQueryFilter;
import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.SharedStatus;
import com.gnoht.tlrl.repository.BookmarkPageRequest;

/**
 * {@link SqlParameterSource} implementation holding named SQL parameters 
 * for all "findAll*" queries.
 */
public class BookmarkRepositorySqlParameterSource 
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
	
	private BookmarkRepositorySqlParameterSource(Pageable pageable) {
		addValue(PAGE_PARAM, pageable.getOffset());
		addValue(PAGE_SIZE_PARAM, pageable.getPageSize());
	}

	@Override
	public BookmarkRepositorySqlParameterSource addValue(String paramName, Object value) {
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
	private BookmarkRepositorySqlParameterSource(
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
		
		if(userId != null) {
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
		} else {
			addValue(SHARED_STATUS_PARAM, true);
		}
	}

	/**
	 * Constructs {@link SqlParameterSource} for *ByOwnerAndTaggedQueries.
	 * 
	 * @param userId
	 * @param tags
	 * @param filter
	 * @param pageable
	 * @return
	 */
	public static BookmarkRepositorySqlParameterSource forByOwnerAndTaggedQueries(
			final Long userId,
			final Set<String> tags,
			final ReadLaterQueryFilter filter,
			final Pageable pageable) 
	{
		BookmarkRepositorySqlParameterSource sqlParameterSource = 
				forByOwnerUntaggedQueries(userId, filter, pageable)
			.addValue(UNTAGGED_PARAM, false);

		if(tags != null) {
			sqlParameterSource
				.addValue(HAS_TAGS_PARAM, !tags.isEmpty())
				.addValue(TAG_COUNT_PARAM, tags.size());
		
			int tagIndex = 0;
			for(String tag: tags) {
				sqlParameterSource.addValue(("tag" + tagIndex++), tag);
			}
		}
		
		return sqlParameterSource;
//		return BookmarkRepositorySqlParameterSource(userId, tags, queryFilter, pageable);
	}
	
	public static BookmarkRepositorySqlParameterSource forByOwnerAndTaggedQuery(
			final Long userId,
			final Set<String> tags,
			final BookmarkPageRequest pageRequest) 
	{
		BookmarkRepositorySqlParameterSource sqlParameterSource =
				forByOwnerUntaggedQuery(userId, pageRequest)
			.addValue(UNTAGGED_PARAM, false);
		
		if(tags != null) {
			sqlParameterSource
				.addValue(HAS_TAGS_PARAM, !tags.isEmpty())
				.addValue(TAG_COUNT_PARAM, tags.size());
			
			int tagIndex = 0;
			for(String tag: tags) {
				sqlParameterSource.addValue(("tag" + tagIndex++), tag);
			}
		}
		
		return sqlParameterSource;
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for *ByOwnerUntagged queries.
	 * 
	 * @param userId
	 * @param filter
	 * @param pageable
	 * @return
	 */
	@Deprecated
	public static BookmarkRepositorySqlParameterSource forByOwnerUntaggedQueries(
			final Long userId,
			final ReadLaterQueryFilter filter,
			final Pageable pageable) 
	{
		BookmarkRepositorySqlParameterSource sqlParameterSource = 
					new BookmarkRepositorySqlParameterSource(pageable)
			.addValue(UNTAGGED_PARAM, true)
			.addValue(HAS_TAGS_PARAM, false)
			.addValue(USER_ID_PARAM, userId);
		
		if(filter != null) {
			if(filter.getReadLaterStatus() == ReadLaterStatus.UNREAD) {
				sqlParameterSource.addValue(READLATER_STATUS_PARAM, ReadLaterStatus.UNREAD);
			}
			
			if(filter.getSharedStatus() == SharedStatus.PUBLIC)
				sqlParameterSource.addValue(SHARED_STATUS_PARAM, true);
			else if(filter.getSharedStatus() == SharedStatus.PRIVATE)
				sqlParameterSource.addValue(SHARED_STATUS_PARAM, false);
		}
		
		return sqlParameterSource;
		//return new BookmarkRepositorySqlParameterSource(userId, NULL_TAGS, filter, pageable);
	}
	
	public static BookmarkRepositorySqlParameterSource forByOwnerUntaggedQuery(
			final Long userId,
			final BookmarkPageRequest pageRequest) 
	{
		BookmarkRepositorySqlParameterSource sqlParameterSource = 
					new BookmarkRepositorySqlParameterSource(pageRequest)
			.addValue(UNTAGGED_PARAM, true)
			.addValue(HAS_TAGS_PARAM, false)
			.addValue(USER_ID_PARAM, userId);
	
		if(pageRequest.hasUnreadSortProperty()) {
			sqlParameterSource.addValue(READLATER_STATUS_PARAM, ReadLaterStatus.UNREAD);
		}
		if(pageRequest.hasPrivateSortProperty()) {
			sqlParameterSource.addValue(SHARED_STATUS_PARAM, false);
		} else if(pageRequest.hasPublicSortProperty()) {
			sqlParameterSource.addValue(SHARED_STATUS_PARAM, true);
		}
		
		return sqlParameterSource;
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for findAllByUserAndTags query.
	 * 
	 * @param userId
	 * @param tags
	 * @param pageable
	 */
	public static BookmarkRepositorySqlParameterSource forByUserAndTagsQueries(
			final Long userId, 
			final Set<String> tags, 
			final Pageable pageable) 
	{
		return forByTagsQueries(tags, pageable)
				.addValue(USER_ID_PARAM, userId)
				.addValue(SHARED_STATUS_PARAM, true);
		
//		BookmarkRepositorySqlParameterSource sqlParameterSource = 
//				new BookmarkRepositorySqlParameterSource(userId, tags, NULL_QUERYFILTER, pageable);
//		sqlParameterSource.addValue(SHARED_STATUS_PARAM, true);
//		return sqlParameterSource;
	}
	
	/**
	 * Constructs {@link SqlParameterSource} for all pageable queries.
	 *  
	 * @param pageable
	 * @return
	 */
	public static BookmarkRepositorySqlParameterSource forPageableQueries(final Pageable pageable) {
		return new BookmarkRepositorySqlParameterSource(pageable);
	}

	/**
	 * Constructs {@link SqlParameterSource} for *ByTags queries.
	 * 
	 * @param tags
	 * @param pageable
	 * @return
	 */
	public static BookmarkRepositorySqlParameterSource forByTagsQueries(
			final Set<String> tags, 
			final Pageable pageable) 
	{
		BookmarkRepositorySqlParameterSource sqlParameterSource =
				new BookmarkRepositorySqlParameterSource(pageable)
				.addValue(UNTAGGED_PARAM, false)
				.addValue(HAS_TAGS_PARAM, false);
		
		if(tags != null) {
			sqlParameterSource
				.addValue(HAS_TAGS_PARAM, !tags.isEmpty())
				.addValue(TAG_COUNT_PARAM, tags.size());
			
			int tagIndex = 0;
			for(String tag: tags) {
				sqlParameterSource.addValue(("tag" + tagIndex++), tag);
			}
		}
		
		return sqlParameterSource;
	}
	
	
	
}
