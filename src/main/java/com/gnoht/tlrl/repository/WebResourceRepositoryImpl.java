package com.gnoht.tlrl.repository;

import static com.gnoht.tlrl.repository.BookmarkRowMapper.DEFAULT_ROW_MAPPER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResource;
import com.opengamma.elsql.ElSqlBundle;
import com.opengamma.elsql.ElSqlConfig;

/**
 * Provides custom {@link WebResourceRepository} 
 */
public class WebResourceRepositoryImpl 
			implements WebResourceCustomRepository {

	private final NamedParameterJdbcOperations jdbcOperations;
	private final ElSqlBundle elsql;
	
	@Inject
	public WebResourceRepositoryImpl(NamedParameterJdbcOperations jdbcOperations) {
		this.jdbcOperations = jdbcOperations;
		this.elsql = ElSqlBundle.of(ElSqlConfig.DEFAULT, WebResourceRepository.class);
	}
	
	@Override
	public WebResource findOneById(Long id) {
		SqlParameterSource paramSource = new MapSqlParameterSource("webResourceId", id);
		return jdbcOperations.query(elsql.getSql("FindBookmarksByWebResource", 
				paramSource), paramSource, webResourceResultSetExtractor);
	}

	/**
	 * 
	 */
	ResultSetExtractor<WebResource> webResourceResultSetExtractor = 
			new ResultSetExtractor<WebResource>() {
		@Override
		public WebResource extractData(ResultSet rs) throws SQLException, DataAccessException {
			WebResource webResource = new WebResource();
			List<Bookmark> bookmarks = new ArrayList<>();
			int row = 0;
			boolean mapped = false;
			while(rs.next()) {
				if(!mapped && rs.getString("type")
						.equalsIgnoreCase("w")) {
					User user = new User();
					user.setId(rs.getLong("user_id"));
					user.setName(rs.getString("user_name"));
					webResource.setUser(user);
					webResource.setUrl(rs.getString("url"));
					webResource.setId(rs.getLong("webpageId"));
					webResource.setDescription(rs.getString("description"));
					//webResource.setDateCreated(rs.getTimestamp("date_created"));
					webResource.setRefCount(rs.getInt("refCount"));
					webResource.setTitle(rs.getString("title"));
					mapped = true;
				} else {
					bookmarks.add(DEFAULT_ROW_MAPPER.mapRow(rs, row++));
				}
			}
			webResource.setBookmarks(bookmarks);
			return webResource;
		}
	};
}
