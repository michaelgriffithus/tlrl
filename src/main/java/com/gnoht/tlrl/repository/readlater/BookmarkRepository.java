package com.gnoht.tlrl.repository.readlater;

import java.util.List;
import java.util.concurrent.Future;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;

import com.gnoht.tlrl.domain.Bookmark;
import com.gnoht.tlrl.domain.User;

public interface BookmarkRepository 
		extends JpaRepository<Bookmark, Long>, BookmarkCustomRepository {

	public Bookmark findOneByUserAndWebResourceUrl(User user, String url);
	
	@Async
	public Future<List<Bookmark>> findByTitle(String title);
	
}
