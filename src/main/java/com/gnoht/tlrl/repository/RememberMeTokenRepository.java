package com.gnoht.tlrl.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.RememberMeToken;

/**
 * {@link Repository} implmentation for managing {@link RememberMeToken}s.
 */
public interface RememberMeTokenRepository extends 
		JpaRepository<RememberMeToken, Long> {
	
	public RememberMeToken findBySeries(String series);
	public RememberMeToken findById(Long id);
	public RememberMeToken findByUserName(String userName);
	public List<RememberMeToken> findAllByUserName(String userName);
	
	@Modifying
	@Transactional(readOnly=false)
	@Query("DELETE FROM remember_me_token r WHERE r.userName = ?1")
	public int deleteByUserName(String userName);
	
	@Modifying
	@Transactional(readOnly=false)
	@Query(nativeQuery=true, value="UPDATE REMEMBER_ME_TOKEN " +
			" SET value =:value, date =:date WHERE series = :series ")
	public int update(@Param("series") String series, 
			@Param("value") String value, @Param("date") Date date);
}
