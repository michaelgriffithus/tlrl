package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gnoht.tlrl.domain.WebResource;

public interface WebPageJpaRepository extends 
		JpaRepository<WebResource, Long>{

	public WebResource findByUrl(String url);
}
