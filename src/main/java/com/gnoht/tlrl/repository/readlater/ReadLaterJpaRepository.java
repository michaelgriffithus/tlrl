package com.gnoht.tlrl.repository.readlater;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;

public interface ReadLaterJpaRepository 
		extends JpaRepository<Bookmark, Long>, ReadLaterCustomRepository {

	public Bookmark findOneByUserAndWebResourceUrl(User user, String url);
	
	@Async
	public Future<List<Bookmark>> findByTitle(String title);
	
}
