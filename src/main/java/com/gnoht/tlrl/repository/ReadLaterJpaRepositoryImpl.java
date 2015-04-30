package com.gnoht.tlrl.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.gnoht.tlrl.controller.Filters;
import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.SharedStatus;
import com.gnoht.tlrl.domain.Tag;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebPage;

import static com.gnoht.tlrl.domain.SharedStatus.*;

public class ReadLaterJpaRepositoryImpl 
		implements ReadLaterCustomRepository {

	private static final Logger LOG = LoggerFactory.getLogger(ReadLaterJpaRepositoryImpl.class);
	
	@Resource private JdbcTemplate jdbcTemplate;
	@Resource private ReadLaterJpaRepository readLaterRepository;
	
	private static final String UNION_STATEMENT = " UNION ALL ";
	private static final String NOT_NULL_USER = " rl.user_id IS NOT NULL ";
	private static final Set<String> UNTAGGED_SET = null;
	private static final Filters DEFAULT_FILTERS = new Filters();

	/*
	 * Finds top 50 Popular ReadLaters. Only public ReadLaters are queried for. 
	 * TODO: should return list of WebPages since we want popular urls, not individual bookmarks
	 * TODO: should share queries/select with findAllByWebpage
	 */
	@Override
	public List<ReadLater> findPopular(Pageable pageable) {
		PreparedStatementCreator findPopularPreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				StringBuilder findRecentQuery = new StringBuilder()
					.append("SELECT w.url, w.id, w.title, url.count AS refCount, w.description, rl.date_created ")
					.append(" FROM webpage w, read_later rl ")
					// TODO: add filter to for public read laters only
					.append("	   LEFT JOIN (SELECT url, COUNT(url), MIN(rl.id) AS read_later_id FROM read_later rl, webpage w ")
					.append("								 WHERE rl.shared = TRUE AND rl.webpage_id = w.id GROUP BY w.url) AS url ")
					.append("				ON url.read_later_id = rl.id ")
					.append(" WHERE rl.shared = TRUE AND url.count IS NOT NULL AND rl.webpage_id = w.id ")
					.append(" ORDER BY refCount DESC, rl.date_created DESC LIMIT 50");
				return conn.prepareStatement(findRecentQuery.toString());
			}
		};
		
		return jdbcTemplate.query(findPopularPreparedStatementCreator, new RowMapper<ReadLater>() {
			@Override
			public ReadLater mapRow(ResultSet rs, int rowCount) throws SQLException {
				ReadLater readLater = new ReadLater(rs.getString("url"));
				readLater.setId(rs.getLong("id"));
				readLater.setDateCreated(rs.getTimestamp("date_created"));
				readLater.setTitle(rs.getString("title"));
				readLater.setRefCount(rs.getInt("refCount"));
				readLater.setDescription(rs.getString("description"));
				return readLater;
			}
		});
	}

	/*
	 * Finds WebPage and all it's ReadLaters. Only public ReadLaters are queried for. 
	 * @see com.gnoht.tlrl.repository.ReadLaterCustomRepository#findAllByWebPage(java.lang.Long)
	 */
	@Override
	public WebPage findAllByWebPage(final Long webPageId) {
		PreparedStatementCreator findAllByWebPagePreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				StringBuilder findAllByWebPageQuery = new StringBuilder()
					.append(buildFindAllByWebPageBaseQuery());
				
				PreparedStatement findAllByWebPagePreparedStatement = 
						conn.prepareStatement(findAllByWebPageQuery.toString());
				
				new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setLong(1, webPageId);
						ps.setLong(2, webPageId);
						ps.setLong(3, webPageId);
						ps.setLong(4, webPageId);
					}
				}.setValues(findAllByWebPagePreparedStatement);
				
				return findAllByWebPagePreparedStatement;
			}
		};
		
		return jdbcTemplate.query(findAllByWebPagePreparedStatementCreator, new ResultSetExtractor<WebPage>() {
				@Override
				public WebPage extractData(ResultSet rs) throws SQLException, DataAccessException {
					WebPage webPage = new WebPage();
					int rowCount = 0;
					boolean webPageProcessed = false;
					while(rs.next()) {
						if(!webPageProcessed && rs.getString("type")
								.equalsIgnoreCase("w")) {
							User user = new User();
							user.setId(rs.getLong("user_id"));
							user.setName(rs.getString("user_name"));
							webPage.setUser(user);
							webPage.setUrl(rs.getString("url"));
							webPage.setId(rs.getLong("webpageId"));
							webPage.setDescription(rs.getString("description"));
							webPage.setDateCreated(rs.getTimestamp("date_created"));
							webPage.setRefCount(rs.getInt("refCount"));
							webPage.setTitle(rs.getString("title"));
							webPageProcessed = true;
						} else {
							webPage.getReadLaters().add(
								findAllByWebPageResultsMapper.mapRow(rs, rowCount++));
						}
					}
					return webPage;
				}
		});
	}

	/*
	 * Finds the most recent ReadLaters. Only queries for public ReadLaters.
	 * @see com.gnoht.tlrl.repository.ReadLaterCustomRepository#findRecent(org.springframework.data.domain.Pageable)
	 */
	@Override
	public List<ReadLater> findRecent(final Pageable pageable) {
		
		PreparedStatementCreator findRecentPreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				// creates the preparedStatement TODO: cache this
				StringBuilder findRecentQuery = new StringBuilder()
					.append(buildFindAllBaseQuery())
					.append(" 	AND rl.shared = TRUE ")
					.append(" ORDER BY rl.date_created DESC, rl.id DESC LIMIT ? ");
				
				PreparedStatement findRecentPreparedStatement = 
						conn.prepareStatement(findRecentQuery.toString());
				
				LOG.debug("findRecentQuery={}", findRecentQuery.toString());
				
				(new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setInt(1, pageable.getPageSize());
					}
				}).setValues(findRecentPreparedStatement);;
				
				// sets the preparedStatement parameters
				return findRecentPreparedStatement;
			}
		};
		return jdbcTemplate.query(findRecentPreparedStatementCreator, findAllResultsMapper);
	}

	/*
	 * Finds most recent tags associated with recent ReadLaters. Only tags from public ReadLaters
	 * are queried for.
	 * @see com.gnoht.tlrl.repository.ReadLaterCustomRepository#findRecentTags()
	 */
	@Override
	public ReadLaterStats findRecentTags() {
		PreparedStatementCreator findRecentTagsPreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				StringBuilder findRecentTagsQuery = new StringBuilder()
					.append("SELECT tag_id, COUNT(tag_id) as count")
					.append("  FROM read_later_tags rlt, read_later rl ")
					.append(" WHERE rl.shared = TRUE AND rl.date_created >= ? ")
					.append("   AND rl.id = rlt.read_later_id ")
					.append(" GROUP BY tag_id");
				PreparedStatement findRecentTagsPreparedStatement = 
						conn.prepareStatement(findRecentTagsQuery.toString());
				(new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setDate(1, new Date(new java.util.Date().getTime()));
					}
				}).setValues(findRecentTagsPreparedStatement);
				return findRecentTagsPreparedStatement;
			}
		};
		
		return jdbcTemplate.query(findRecentTagsPreparedStatementCreator, new ResultSetExtractor<ReadLaterStats>() {
			@Override
			public ReadLaterStats extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				List<Tag> tags = new ArrayList<Tag>();
				while(rs.next()) 
					tags.add(new Tag(rs.getString("tag_id"), rs.getInt("count")));
				return new ReadLaterStats(tags, Collections.<Tag>emptyList(), 0);
			}
		});
	}

	@Override
	public ReadLaterStats findPopularTags() {
		PreparedStatementCreator findPopularTagsPreparedStatementCreator = new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				StringBuilder findPopularTagsQuery = new StringBuilder()
				.append("SELECT tag_id, COUNT(tag_id) ")
				.append("  FROM read_later_tags rlt, read_later rl ")
				.append(" WHERE rl.shared = TRUE AND rl.id = rlt.read_later_id ")
				.append(" GROUP BY tag_id ")
				.append(" ORDER BY count DESC LIMIT 10");
				return conn.prepareStatement(findPopularTagsQuery.toString());
			}
		};
		
		return jdbcTemplate.query(findPopularTagsPreparedStatementCreator, new ResultSetExtractor<ReadLaterStats>() {
			@Override
			public ReadLaterStats extractData(ResultSet rs) throws SQLException,
					DataAccessException {
				List<Tag> tags = new ArrayList<Tag>();
				while(rs.next()) 
					tags.add(new Tag(rs.getString("tag_id"), rs.getInt("count")));
				return new ReadLaterStats(tags, Collections.<Tag>emptyList(), 0);
			}
		});
	}

	@Override
	public List<ReadLater> findAllByTags(Set<String> tags, Pageable pageable) {
		return jdbcTemplate.query(buildFindAllPreparedStatementCreator(null, false, DEFAULT_FILTERS, tags, pageable), 
				findAllResultsMapper);
	}
	
	@Override
	public List<ReadLater> findAllByUserAndTags(User user, Set<String> tags, Pageable pageable) {
		return jdbcTemplate.query(buildFindAllPreparedStatementCreator(user.getId(), false, DEFAULT_FILTERS, tags, pageable), 
				findAllResultsMapper);
	}

	@Override
	public List<ReadLater> findAllByOwnerAndTagged(User owner, Filters filters, Set<String> tags, Pageable pageable) {
		return jdbcTemplate.query(buildFindAllPreparedStatementCreator(
				owner.getId(), true, filters, tags, pageable), findAllResultsMapper);
	}

	/*
	 * @see com.gnoht.tlrl.repository.ReadLaterCustomRepository#findAllByOwnerAndUntagged(
	 * 		com.gnoht.tlrl.domain.User, com.gnoht.tlrl.domain.SharedState, org.springframework.data.domain.Pageable)
	 */
	@Override
	public List<ReadLater> findAllByOwnerAndUntagged(
				User owner, Filters filters, Pageable pageable) {
		
		LOG.debug("Starting findAllByOwnerAndUntagged(): owner={}, filters={}", owner, filters);
		
		return jdbcTemplate.query(buildFindAllPreparedStatementCreator(
				owner.getId(), true, filters, UNTAGGED_SET, pageable), findAllResultsMapper);
	}

	/**
	 * Builds the PreparedStatementCreator for our findAll queries.
	 * 
	 * @param userId Id of User to filter by
	 * @param tags Set of Tags to filter by
	 * @param pageable
	 * @return
	 */
	private PreparedStatementCreator buildFindAllPreparedStatementCreator(final Long userId, 
				final boolean isOwner, final Filters filters, final Set<String> tags, final Pageable pageable) {
		
		LOG.debug("Starting buildFindAllPreparedStatementCreator(): userId={}, "
				+ "isOwner={}, filters={}, tags={}", userId, isOwner, filters, tags);
		
		return new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				// creates the preparedStatement TODO: cache this
				StringBuilder findAllQuery = new StringBuilder()
					.append(buildFindAllBaseQuery())
					.append(buildFindAllFilter(userId, isOwner, filters, tags));
				
				PreparedStatement findAllPreparedStatement = conn.prepareStatement(findAllQuery.toString());
				
				LOG.debug("findAllQuery={}", findAllQuery.toString());
				
				// sets the preparedStatement parameters
				buildFindAllPreparedStatementSetter(userId, tags, pageable)
					.setValues(findAllPreparedStatement);
				
				return findAllPreparedStatement;
			}
		};
	}
	
	private String buildFindAllBaseQuery() {
		return new StringBuilder()
			.append("SELECT rl.id, rl.title, rl.user_id, u.name AS user_name, rl.shared, rl.description, rlt0.tag_id AS tag0, rl.date_created, rl.read_later_status, ")
			.append("		rlt1.tag_id AS tag1, rlt2.tag_id AS tag2, rlt3.tag_id AS tag3, rlt4.tag_id AS tag4, ")
			.append("		w.id AS webpageId, w.url")
			.append("  FROM tlrl_user u, webpage w, read_later rl ")
			.append("  		LEFT JOIN read_later_tags rlt0 ON rlt0.read_later_id = rl.id AND rlt0.idx = 0 ")
			.append("  		LEFT JOIN read_later_tags rlt1 ON rlt1.read_later_id = rl.id AND rlt1.idx = 1 ")
			.append("  		LEFT JOIN read_later_tags rlt2 ON rlt2.read_later_id = rl.id AND rlt2.idx = 2 ")
			.append("  		LEFT JOIN read_later_tags rlt3 ON rlt3.read_later_id = rl.id AND rlt3.idx = 3 ")
			.append("  		LEFT JOIN read_later_tags rlt4 ON rlt4.read_later_id = rl.id AND rlt4.idx = 4 ")
			.append(" WHERE w.id = rl.webpage_id AND u.id = rl.user_id ").toString();
	}

	private String buildFindAllByWebPageBaseQuery() {
		return new StringBuilder()
			.append("(SELECT 'W' AS type, w.id, w.title, w.url, w.user_id, u.name AS user_name, w.description, true AS shared, rl.date_created, ")
			.append("			NULL AS tag0, NULL AS tag1, NULL AS tag2, NULL AS tag3, NULL AS tag4, w.id AS webpageId, refCount ")
			.append("		FROM tlrl_user u, read_later rl, (SELECT MIN(id) AS id FROM read_later WHERE webpage_id = ?) AS first_rl, webpage w ")
			.append("			LEFT OUTER JOIN (SELECT webpage_id, COUNT(id)-1 AS refCount FROM read_later WHERE webpage_id = ? AND shared = TRUE GROUP BY (webpage_id)) refc ON refc.webpage_id = w.id ")
			.append("		WHERE w.id = ? AND first_rl.id = rl.id AND rl.user_id = u.id) ")
			.append("UNION")
			//-- gets the Readlaters belonging to the WebPage above
			.append("(SELECT 'R' AS type, rl.id, rl.title, w.url, rl.user_id, u.name AS user_name, rl.description, rl.shared, rl.date_created, ")
			.append("			rlt0.tag_id AS tag0, rlt1.tag_id AS tag1, rlt2.tag_id AS tag2, rlt3.tag_id AS tag3, rlt4.tag_id AS tag4, w.id AS webpageId, 1 AS refCount ")
			.append("		FROM tlrl_user u, webpage w, read_later rl ")
			.append("			LEFT JOIN read_later_tags rlt0 ON rlt0.read_later_id = rl.id AND rlt0.idx = 0 ")   		
			.append("			LEFT JOIN read_later_tags rlt1 ON rlt1.read_later_id = rl.id AND rlt1.idx = 1 ")   		
			.append("			LEFT JOIN read_later_tags rlt2 ON rlt2.read_later_id = rl.id AND rlt2.idx = 2 ")   		
			.append("			LEFT JOIN read_later_tags rlt3 ON rlt3.read_later_id = rl.id AND rlt3.idx = 3 ")   		
			.append("			LEFT JOIN read_later_tags rlt4 ON rlt4.read_later_id = rl.id AND rlt4.idx = 4 ")  
			.append("	 WHERE rl.shared = TRUE AND w.id = ? AND w.id = rl.webpage_id AND u.id = rl.user_id) ")
			.append("  ORDER BY type DESC, id DESC ").toString();		
	}
	
	
  /**
   * Builds the IN clause in our findAll PreparedStatements.
   *  
   * 	rl.id IN 
   * 		(SELECT read_later_id FROM (
   * 				SELECT read_later_id, COUNT(*) FROM read_later rl, read_later_tags rlt 
   * 			 	 WHERE rl.user_id = ? AND rl.id = rlt.read_later_id 
   * 				 	 AND (rlt.tag_id = '' OR tag_id = ? ...)
   * 			 	 GROUP BY read_later_id) AS t 
   * 			WHERE t.count = 1)
   * 
   * @param tags
   * @return
   */
	private String buildFindAllFilter(final Long userId, final boolean isOwner, final Filters filters, final Set<String> tags) {
		StringBuilder findAllQueryFilter = new StringBuilder();
		if(tags == null) {
			findAllQueryFilter.append(" AND rl.user_id = ? ")
				.append(" AND rlt0.read_later_id IS NULL ")
				.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
				.append(getUnreadFilter(filters.getReadLaterStatus()));
		} else if(tags.isEmpty()) {
			findAllQueryFilter.append(" AND ")
				.append(userId == null ? NOT_NULL_USER : " rl.user_id = ? ")
				.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
				.append(getUnreadFilter(filters.getReadLaterStatus()));
		} else {
			findAllQueryFilter.append(" AND rl.id IN ")
				.append(buildUserAndTagsFilter(userId, isOwner, filters, tags));
		}
		findAllQueryFilter.append(" ORDER BY date_created DESC, rl.id DESC LIMIT ? OFFSET ? ");
		return findAllQueryFilter.toString();
	}

	/**
	 * (SELECT read_later_id FROM (
	 * 			SELECT rlt.read_later_id, COUNT(rlt.read_later_id) 	 
	 * 				FROM read_later rl, read_later_tags rlt 	
	 * 		 	 WHERE rl.description != ? AND rl.id = rlt.read_later_id ) 
	 * 	 GROUP BY rlt.read_later_id) AS t)
	 * @param tags
	 * @return
	 */
	private String buildUserAndTagsFilter(Long userId, boolean isOwner, Filters filters, Set<String> tags) {
		StringBuilder filterBuilder = new StringBuilder();
			filterBuilder
					.append("(SELECT read_later_id FROM (")
					.append("			SELECT rlt.read_later_id, COUNT(rlt.read_later_id) ")
					.append("	 			FROM read_later rl, read_later_tags rlt ")
					.append("			 WHERE ").append(userId == null ? NOT_NULL_USER : " rl.user_id = ? ")
					.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
					.append(getUnreadFilter(filters.getReadLaterStatus()))
					.append("				AND rl.id = rlt.read_later_id ");
			
			if(tags != null && !tags.isEmpty()) {
				filterBuilder.append("		AND (rlt.tag_id = ''");
				for(int i=0; i < tags.size(); i++)  
					filterBuilder.append(" OR rlt.tag_id = ? ");
				filterBuilder.append(") GROUP BY rlt.read_later_id) AS t WHERE t.count = ?)");
			} else {
				if(tags == null) 
					filterBuilder.append(" AND rlt0.read_later_id IS NULL ");
				filterBuilder.append("GROUP BY rlt.read_later_id) AS t) ");
			}
		return filterBuilder.toString();
	}
	
	
	@Override
	public ReadLaterStats findReadLaterStatsByTags(Set<String> tags) {
		return jdbcTemplate.query(buildStatsPreparedStatementCreator(null, false, DEFAULT_FILTERS, tags), 
				findReadLaterStatsResultsExtractor);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByUserAndTags(User user, Set<String> tags) {
		return jdbcTemplate.query(buildStatsPreparedStatementCreator(user.getId(), false, DEFAULT_FILTERS, tags), 
				findReadLaterStatsResultsExtractor);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByOwnerAndTagged(User owner, Filters filters, Set<String> tags) {
		return jdbcTemplate.query(buildStatsPreparedStatementCreator(
				owner.getId(), true, filters, tags), findReadLaterStatsResultsExtractor);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByOwnerAndUntagged(User owner, Filters filters) {
		return jdbcTemplate.query(buildStatsPreparedStatementCreator(
				owner.getId(), true, filters, UNTAGGED_SET), findReadLaterStatsResultsExtractor);
	}

	private PreparedStatementCreator buildStatsPreparedStatementCreator(
			final Long userId, final boolean isOwner, final Filters filters, final Set<String> tags) {
		LOG.debug("Starting buildStatsPreparedStatementCreator(): userId={}, isOwner={}", userId, isOwner);
		return new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn)
					throws SQLException {
				StringBuilder statsQueryBuilder = new StringBuilder()
						.append(buildAllTagsQuery(userId, isOwner, filters))
						.append(UNION_STATEMENT)
						.append(buildCountQuery(userId, isOwner, filters, tags));
				
				if(tags != null && !tags.isEmpty()) {
					statsQueryBuilder
						.append(UNION_STATEMENT)
						.append(buildRelatedTagsQuery(userId, isOwner, filters, tags));
				}
				
				statsQueryBuilder.append(" ORDER BY type DESC, count DESC ");
				
				PreparedStatement statsPreparedStatement = conn
						.prepareStatement(statsQueryBuilder.toString());
				
				buildStatsPreparedStatementSetter(userId, tags)
					.setValues(statsPreparedStatement);
				
				return statsPreparedStatement;
			}
		};
	}
	
	private PreparedStatementSetter buildStatsPreparedStatementSetter(
			final Long userId, final Set<String> tags) {
		return new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int paramIndex = 1; 
				if(userId != null) {
					// set params for all tags query (only filters by user)
					ps.setLong(paramIndex++, userId); //ps.setLong(paramIndex++, userId);
				}
				
				// set params for count/total query (filters by user and tags if applicable)
				paramIndex = setUserAndTagsFilterPreparedStatementParams(userId, tags, paramIndex, ps);
				if(tags != null && !tags.isEmpty()) {
					// set params for related tags query
					paramIndex = setUserAndTagsFilterPreparedStatementParams(userId, tags, paramIndex, ps);
					for(String tag: tags) {
						ps.setString(paramIndex++, tag);
					}
				}
			}
		};
	}
	
	/**
select count(r.read_later_id)
  from (select read_later_id from (
      select read_later_id, count(*) from read_later rl, read_later_tags rlt
       where rl.description != '' and rl.id = rlt.read_later_id 
         and (tag_id = '' OR tag_id = 'java' OR tag_id = 'spring')
       group by read_later_id) as T where t.count = 2) as r	 * 
	 * @param tags
	 * @return
	 */
	private String buildCountQuery(Long userId, boolean isOwner, Filters filters, Set<String> tags) {
		StringBuilder countQuery = new StringBuilder(
				"SELECT 'TOTAL' AS type, 'Total ReadLaters' AS name ");

		if(tags == null) {
			countQuery.append(", COUNT(rl.id)")
			.append("  FROM read_later rl ")
			.append("			LEFT OUTER JOIN read_later_tags rlt ON rlt.read_later_id = rl.id AND rlt.idx = 0 ")
			.append(" WHERE ").append(userId == null ? NOT_NULL_USER : " rl.user_id = ? ")
			.append("		AND rlt.read_later_id IS NULL ")
			.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
			.append(getUnreadFilter(filters.getReadLaterStatus()));
		} else if(tags == null || tags.isEmpty()) {
			countQuery.append(", COUNT(rl.id)")
				.append("  FROM read_later rl")
				.append(" WHERE ").append(userId == null ? NOT_NULL_USER : " rl.user_id = ? ")
				.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
				.append(getUnreadFilter(filters.getReadLaterStatus()));
		} else {
			countQuery.append(", COUNT(r.read_later_id) ")
				.append("  FROM ").append(buildUserAndTagsFilter(userId, isOwner, filters, tags)).append(" AS r ");
		}
		return countQuery.toString();
	}

	private String getIsSharedFilter(boolean isOwner, SharedStatus sharedStatus) {
		return ((!isOwner || sharedStatus == PUBLIC) ? " AND rl.shared = TRUE " : 
				(sharedStatus == PRIVATE ? " AND rl.shared = FALSE " : ""));
	}
	
	/**
select rlt.tag_id, count(rlt.tag_id)
  from (select read_later_id from (
      select read_later_id, count(*) from read_later rl, read_later_tags rlt
       where rl.description != '' and rl.id = rlt.read_later_id 
         and (tag_id = '' OR tag_id = 'java' OR tag_id = 'spring')
       group by read_later_id) as T where t.count = 2) as t
   inner join read_later_tags rlt on rlt.read_later_id = t.read_later_id
 where rlt.tag_id != 'java' and rlt.tag_id != 'spring'
 group by rlt.tag_id
 	 * 
	 * @param tags
	 * @return
	 */
	private String buildRelatedTagsQuery(Long userId, boolean isOwner, Filters filters, Set<String> tags) {
		StringBuilder relatedTagsQuery = new StringBuilder()
				.append("SELECT 'RELATED' AS type, rlt.tag_id AS name, COUNT(rlt.tag_id) ");
		if(tags == null || tags.isEmpty()) {
			relatedTagsQuery
				.append("	 FROM read_later rl, read_later_tags rlt ")
				.append("	WHERE ").append(userId == null ? NOT_NULL_USER : " rl.user_id = ? ")
				.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
				.append(getUnreadFilter(filters.getReadLaterStatus()))
				.append("		AND rl.id = rlt.read_later_id");
		} else {
			relatedTagsQuery
				.append("  FROM ").append(buildUserAndTagsFilter(userId, isOwner, filters, tags)).append(" AS f ")
				.append("			INNER JOIN read_later_tags rlt ON rlt.read_later_id = f.read_later_id ")
				.append(" WHERE (");
			for(int i=0; i < tags.size(); i++) {
				if(i > 0)
					relatedTagsQuery.append(" AND ");
				relatedTagsQuery.append(" rlt.tag_id != ? ");
			}
			relatedTagsQuery.append(") ");
		}
		relatedTagsQuery.append(" GROUP BY rlt.tag_id ");
		
		LOG.debug("relatedTagsQuery={}", relatedTagsQuery);
		
		return relatedTagsQuery.toString();
	}
	
	private String getUnreadFilter(ReadLaterStatus readLaterStatus) {
		return readLaterStatus == ReadLaterStatus.UNREAD ? 
				" AND rl.read_later_status = 'UNREAD' " : "";
	}
	
	private String buildAllTagsQuery(Long userId, boolean isOwner, Filters filters) {
		String allTagsQuery = new StringBuilder()
				.append("SELECT 'ALL' AS type, rlt.tag_id AS name, COUNT(rlt.tag_id) ")
				.append("  FROM read_later rl, read_later_tags rlt ")
				.append(" WHERE ").append(userId == null ? NOT_NULL_USER : " rl.user_id = ? ")
				.append(getIsSharedFilter(isOwner, filters.getSharedStatus()))
				.append(getUnreadFilter(filters.getReadLaterStatus()))
				.append("   AND rl.id = rlt.read_later_id ")
				.append(" GROUP BY rlt.tag_id ")
			.toString();
		
		LOG.debug("allTagsQuery={}", allTagsQuery);
		return allTagsQuery;
	}

	/**
	 * Builds the PreparedStatementSetter for our findAll queries.
	 *  
	 * @param userId
	 * @param tags
	 * @param pageable
	 * @return
	 */
	private PreparedStatementSetter buildFindAllPreparedStatementSetter(
			final Long userId, final Set<String> tags, final Pageable pageable) {
		return new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int paramIndex = setUserAndTagsFilterPreparedStatementParams(userId, tags, 1, ps);
				ps.setInt(paramIndex++, pageable.getPageSize());
				ps.setInt(paramIndex, pageable.getOffset());
			}
		};
	}
	
	private int setUserAndTagsFilterPreparedStatementParams(
				final Long userId, final Set<String> tags, int paramIndex, PreparedStatement ps) 
			throws SQLException {
		// set the userId param
		if(userId != null) {
			ps.setLong(paramIndex++, userId);
		}
		if(tags != null && !tags.isEmpty()) {
			// we may filter on 0-5 tags, so set their param values
			for(String tag: tags)
				ps.setString(paramIndex++, tag);
			// the final param is a count of the tags we just filtered by
			ps.setInt(paramIndex++, tags.size());
		}
		return paramIndex;
	}
	
	private class ReadLaterRowMapper implements RowMapper<ReadLater> {

		@Override
		public ReadLater mapRow(ResultSet rs, int arg1) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("user_id"));
			user.setName(rs.getString("user_name"));
			
			WebPage webPage = new WebPage(user, rs.getString("url"));
			webPage.setId(rs.getLong("webpageId"));
			
			ReadLater readLater = new ReadLater(user, webPage);
			readLater.setId(rs.getLong("id"));
			readLater.setDescription(rs.getString("description"));
			readLater.setTitle(rs.getString("title"));
			readLater.setShared(rs.getBoolean("shared"));
			readLater.setDateCreated(rs.getTimestamp("date_created"));
			getTagColumn("tag0", rs, readLater);
			getTagColumn("tag1", rs, readLater);
			getTagColumn("tag2", rs, readLater);
			getTagColumn("tag3", rs, readLater);
			getTagColumn("tag4", rs, readLater);
			return readLater;
		}
		
		protected void getTagColumn(String tagName, ResultSet rs, ReadLater readLater) 
				throws SQLException {
			String tagId = rs.getString(tagName);
			if(tagId != null) {
				readLater.getTags().add(new Tag(tagId));
			}
		}
		
	}
	
	private RowMapper<ReadLater> findAllByWebPageResultsMapper = new ReadLaterRowMapper();
	
	private RowMapper<ReadLater> findAllResultsMapper = new ReadLaterRowMapper() {
		@Override
		public ReadLater mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReadLater readLater = super.mapRow(rs, rowNum);
			readLater.setReadLaterStatus(ReadLaterStatus.valueOf(rs.getString("read_later_status")));
			return readLater;
		}
	};

	private ResultSetExtractor<ReadLaterStats> findReadLaterStatsResultsExtractor =
			new ResultSetExtractor<ReadLaterStats>() {
				@Override
				public ReadLaterStats extractData(ResultSet rs) throws SQLException,
						DataAccessException {
					int resultsCount = 0;
					List<Tag> allTags = new ArrayList<Tag>();
					List<Tag> relatedTags = new ArrayList<Tag>();
					while(rs.next()) {
						String type = rs.getString("type");
						String name = rs.getString("name");
						int count = rs.getInt("count");
						
						if(type.equals("TOTAL")) {
							resultsCount = count;
						} else if(type.equals("ALL")) {
							allTags.add(new Tag(name, count));
						} else {
							relatedTags.add(new Tag(name, count));
						}
					}
					return new ReadLaterStats(allTags, relatedTags, resultsCount);
				}
	};

}
