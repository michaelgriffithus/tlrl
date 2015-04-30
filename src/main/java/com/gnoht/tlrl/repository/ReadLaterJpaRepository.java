package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.ReadLater;
import com.gnoht.tlrl.domain.User;

public interface ReadLaterJpaRepository 
		extends JpaRepository<ReadLater, Long>, ReadLaterCustomRepository {

	public ReadLater findOneByUserAndWebPageUrl(User user, String url);
	
}
