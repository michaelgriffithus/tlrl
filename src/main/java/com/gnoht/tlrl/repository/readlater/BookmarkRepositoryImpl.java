package com.gnoht.tlrl.repository.readlater;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.gnoht.tlrl.controller.ReadLaterQueryFilter;
import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.ReadLaterStats;
import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.Tag;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebUrl;
import com.opengamma.elsql.ElSqlBundle;
import com.opengamma.elsql.ElSqlConfig;

import static com.gnoht.tlrl.repository.readlater.BookmarkRepositorySqlParameterSource.*;

public class BookmarkRepositoryImpl 
		implements BookmarkCustomRepository {

	private static final Logger LOG = LoggerFactory.getLogger(BookmarkRepositoryImpl.class);
	
	@Resource private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static Pageable defaultPageable = new PageRequest(1, 50);
	private static SqlParameterSource defaultSqlParameterSource = forPageableQueries(defaultPageable);
	
	private ElSqlBundle bundle = ElSqlBundle.
			of(ElSqlConfig.DEFAULT, BookmarkRepository.class);
	
	@Override
	public List<Bookmark> findPopular(Pageable pageable) {
		LOG.debug("Starting findPopular: pageable={}", pageable);
		return namedParameterJdbcTemplate.query(bundle.getSql("FindPopularQuery"), 
				defaultSqlParameterSource, popularReadLaterRowMapper);
	}

	@Override
	public Page<Bookmark> findPopularByWebUrl(Long id) {
		LOG.debug("Starting findPopularByWebUrl(): id={}", id);
		SqlParameterSource paramSource = new MapSqlParameterSource("weburlId", id);
		return namedParameterJdbcTemplate.query(bundle.getSql("FindBookmarksByWebUrl", 
				paramSource), paramSource, popularBookmarkResultSetExtractor);
	}

	@Override
	public List<Bookmark> findRecent(final Pageable pageable) {
		String sql = bundle.getSql("FindRecentQuery"); 
		return namedParameterJdbcTemplate.query(
				sql, defaultSqlParameterSource, publicReadLaterRowMapper);
	}

	@Override
	public ReadLaterStats findRecentTags() {
		return namedParameterJdbcTemplate.query(bundle.getSql("FindRecentRelatedTagsQuery"), readLaterStatsRsExtractor);
	}

	@Override
	public ReadLaterStats findPopularTags() {
		return namedParameterJdbcTemplate.query(bundle.getSql("FindPopularRelatedTagsQuery"),
				defaultSqlParameterSource, readLaterStatsRsExtractor);
	}

	@Override
	public List<Bookmark> findAllByTags(Set<String> tags, Pageable pageable) {
		LOG.info("Starting findAllByTags(): tags={}, pageable={}", tags, pageable);
		SqlParameterSource paramSource = new BookmarkRepositorySqlParameterSource(tags, pageable);
		if(LOG.isDebugEnabled()) {
			LOG.debug("sql={}", bundle.getSql("FindAllQuery", paramSource));
		}
		return namedParameterJdbcTemplate.query(bundle.getSql("FindAllQuery", paramSource), paramSource, publicReadLaterRowMapper);
	}
	
	@Override
	public List<Bookmark> findAllByUserAndTags(User user, Set<String> tags, Pageable pageable) {
		SqlParameterSource paramSource = forByUserAndTagsQueries(user.getId(), tags, pageable);
		return namedParameterJdbcTemplate.query(
				bundle.getSql("FindAllQuery", paramSource),  paramSource, publicReadLaterRowMapper);
	}

	@Override
	public List<Bookmark> findAllByOwnerAndTagged(User owner, 
			ReadLaterQueryFilter queryFilter, Set<String> tags, Pageable pageable) 
	{
		SqlParameterSource paramSource = forByOwnerAndTaggedQueries(owner.getId(), tags, queryFilter, pageable);
		return namedParameterJdbcTemplate.query(
				bundle.getSql("FindAllQuery", paramSource), paramSource, privateReadLaterRowMapper);
	}

	@Override
	public List<Bookmark> findAllByOwnerAndUntagged(User owner, 
			ReadLaterQueryFilter queryFilter, Pageable pageable) 
	{
		SqlParameterSource paramSource = forByOwnerUntaggedQueries(owner.getId(), queryFilter, pageable); 
		return namedParameterJdbcTemplate.query(
				bundle.getSql("FindAllQuery", paramSource), paramSource, privateReadLaterRowMapper);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByTags(Set<String> tags) {
		SqlParameterSource paramSource = forByTagsQueries(tags, defaultPageable);
		String sql = bundle.getSql("StatsQuery", paramSource);
		return namedParameterJdbcTemplate.query(sql, paramSource, findReadLaterStatsResultsExtractor);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByUserAndTags(User user, Set<String> tags) {
		SqlParameterSource paramSource = forByUserAndTagsQueries(user.getId(), tags, defaultPageable);
		return namedParameterJdbcTemplate.query(bundle.getSql("StatsQuery", paramSource), paramSource, findReadLaterStatsResultsExtractor);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByOwnerAndTagged(User owner, ReadLaterQueryFilter readLaterQueryFilter, Set<String> tags) {
		SqlParameterSource paramSource = forByOwnerAndTaggedQueries(owner.getId(), tags, readLaterQueryFilter, defaultPageable);
		return namedParameterJdbcTemplate.query(bundle.getSql("StatsQuery", paramSource), paramSource, findReadLaterStatsResultsExtractor);
	}
	
	@Override
	public ReadLaterStats findReadLaterStatsByOwnerAndUntagged(User owner, ReadLaterQueryFilter readLaterQueryFilter) {
		SqlParameterSource paramSource = forByOwnerUntaggedQueries(owner.getId(), readLaterQueryFilter, defaultPageable);
		return namedParameterJdbcTemplate.query(bundle.getSql("StatsQuery", paramSource), paramSource, findReadLaterStatsResultsExtractor);
	}

	/**
	 * Maps a row of {@link ResultSet} data to a ReadLater.
	 */
	class SimpleReadLaterRowMapper implements RowMapper<Bookmark> {
		
		@Override
		public Bookmark mapRow(ResultSet rs, int arg1) throws SQLException {
			Bookmark bookmark = new Bookmark();
			bookmark.setId(rs.getLong("id"));
			bookmark.setDescription(rs.getString("description"));
			bookmark.setTitle(rs.getString("title"));
			bookmark.setDateCreated(rs.getTimestamp("date_created"));

			User user = new User();
			user.setId(rs.getLong("user_id"));
			user.setName(rs.getString("user_name"));
			bookmark.setUser(user);
			
			WebUrl webUrl = new WebUrl(rs.getString("url"));
			webUrl.setId(rs.getLong("weburlId"));
			bookmark.setWebPage(webUrl);
			
			return bookmark;
		}
		
		/**
		 * 
		 * @param tagName
		 * @param rs
		 * @param bookmark
		 * @throws SQLException
		 */
		void getTagColumn(String tagName, ResultSet rs, Bookmark bookmark) 
				throws SQLException {
			String tagId = rs.getString(tagName);
			if(tagId != null) {
				bookmark.getTags().add(new Tag(tagId));
			}
		}
	};

	/**
	 * {@link RowMapper} implementation that maps result set data
	 * from private "findAll" queries.
	 */
	class PrivateReadLaterRowMapper extends PublicReadLaterRowMapper {
		@Override
		public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
			Bookmark bookmark = super.mapRow(rs, rowNum);
			bookmark.setShared(rs.getBoolean("shared"));
			bookmark.setReadLaterStatus(ReadLaterStatus.valueOf(rs.getString("read_later_status")));
			return bookmark;
		}
	};
	
	/**
	 * {@link RowMapper} implementation that maps result set data
	 * from public "findAll" queries. 
	 */
	class PublicReadLaterRowMapper extends SimpleReadLaterRowMapper {
		@Override
		public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
			Bookmark bookmark = super.mapRow(rs, rowNum);
			getTagColumn("tag0", rs, bookmark);
			getTagColumn("tag1", rs, bookmark);
			getTagColumn("tag2", rs, bookmark);
			getTagColumn("tag3", rs, bookmark);
			getTagColumn("tag4", rs, bookmark);
			return bookmark;
		}
	};

	/**
	 * {@link RowMapper} implementation that maps result set data
	 * from "find popular" queries.
	 */
	protected final RowMapper<Bookmark> popularReadLaterRowMapper = new SimpleReadLaterRowMapper() {
		@Override
		public Bookmark mapRow(ResultSet rs, int rowNum) throws SQLException {
			Bookmark bookmark = super.mapRow(rs, rowNum);
			String webResourceTitle = rs.getString("wtitle");
			if(webResourceTitle != null) {
				bookmark.setTitle(webResourceTitle);
			}
			bookmark.setRefCount(rs.getInt("refCount"));
			return bookmark;
		}
	};
	
	
	protected final RowMapper<Bookmark> privateReadLaterRowMapper = new PrivateReadLaterRowMapper();
	protected final RowMapper<Bookmark> publicReadLaterRowMapper = new PublicReadLaterRowMapper();
	
	ResultSetExtractor<Page<Bookmark>> popularBookmarkResultSetExtractor = 
			new ResultSetExtractor<Page<Bookmark>>() {
		@Override
		public Page<Bookmark> extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			List<Bookmark> content = new ArrayList<>();
			int rowCount = 0;
			while(rs.next()) {
				Bookmark bookmark = publicReadLaterRowMapper.mapRow(rs, rowCount);
				content.add(bookmark);
				rowCount++;
			}
			return new PageImpl<>(content, defaultPageable, content.size());
		}
	};
	
	private ResultSetExtractor<ReadLaterStats> readLaterStatsRsExtractor =
				new ResultSetExtractor<ReadLaterStats>() {
		@Override
		public ReadLaterStats extractData(ResultSet rs) throws SQLException, DataAccessException {
			List<Tag> tags = new ArrayList<Tag>();
			while(rs.next()) 
				tags.add(new Tag(rs.getString("tag_id"), rs.getInt("count")));
			return new ReadLaterStats(tags, Collections.<Tag>emptyList(), 0);
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
						String name = rs.getString("name");
						int count = rs.getInt("count");
						
						switch (rs.getString("type")) {
						case "TOTAL":
							resultsCount = count;
							break;
						case "ALL":
							allTags.add(new Tag(name, count));
							break;
						default:
							relatedTags.add(new Tag(name, count));
							break;
						}
					}
					return new ReadLaterStats(allTags, relatedTags, resultsCount);
				}
	};

}
