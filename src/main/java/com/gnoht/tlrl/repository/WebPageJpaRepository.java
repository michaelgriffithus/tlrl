package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gnoht.tlrl.domain.WebPage;

public interface WebPageJpaRepository extends 
		JpaRepository<WebPage, Long>{

	public WebPage findByUrl(String url);
}
