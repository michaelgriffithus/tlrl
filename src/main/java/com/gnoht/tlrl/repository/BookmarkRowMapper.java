package com.gnoht.tlrl.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.ReadLaterStatus;
import com.gnoht.tlrl.domain.Tag;
import com.gnoht.tlrl.domain.User;
import com.gnoht.tlrl.domain.WebResourceNew;

public abstract class BookmarkRowMapper implements RowMapper<Bookmark> {

	static final String[] tagColNames = {"tag0", "tag1", "tag2", "tag3", "tag4"};
	
	private BookmarkRowMapper() {}
	
	@Override
	public Bookmark mapRow(ResultSet rs, int row) throws SQLException {
		Bookmark bookmark = new Bookmark();
		bookmark.setId(rs.getLong("id"));
		bookmark.setDescription(rs.getString("description"));
		bookmark.setTitle(rs.getString("title"));
		bookmark.setDateCreated(rs.getTimestamp("date_created"));
		return bookmark;
	}
	
	/**
	 * 
	 * @param rs
	 * @param bookmark
	 * @throws SQLException
	 */
	protected void mapTags(ResultSet rs, Bookmark bookmark) 
			throws SQLException {
		List<Tag> tags = new ArrayList<>(/*Bookmark.MAX_NUMBER_OF_TAGS*/);
		for(String colName: tagColNames) {
			String tagId = rs.getString(colName);
			if(tagId != null) {
				tags.add(new Tag(tagId));
			}
		}
		bookmark.setTags(tags);
	}
	
	public static final RowMapper<Bookmark> DEFAULT_ROW_MAPPER = new DefaultBookmarkRowMapper();
	public static final RowMapper<Bookmark> DETAILED_ROW_MAPPER = new DetailedBookmarkRowMapper();

	/**
	 * 
	 */
	private static class DefaultBookmarkRowMapper 
				extends BookmarkRowMapper {
		@Override
		public Bookmark mapRow(ResultSet rs, int row) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("user_id"));
			user.setName(rs.getString("user_name"));
			
			WebResourceNew webResource = WebResourceNew.builder()
					.id(rs.getLong("webresourceId"))
					.user(user)
					.url(rs.getString("url"))
					.build();
			
			Bookmark bookmark = super.mapRow(rs, row);
			//bookmark.setWebResource(webResource);
			bookmark.setUser(user);
			mapTags(rs, bookmark);
			
			return bookmark;
		}
	};
	
	/**
	 * 
	 */
	private static class DetailedBookmarkRowMapper 
				extends DefaultBookmarkRowMapper {
		@Override
		public Bookmark mapRow(ResultSet rs, int row) throws SQLException {
			Bookmark bookmark = super.mapRow(rs, row);
			bookmark.setShared(rs.getBoolean("shared"));
			//bookmark.setStatus(ReadLaterStatus.valueOf(rs.getString("read_later_status")));
			return bookmark;
		}
		
	}
}
