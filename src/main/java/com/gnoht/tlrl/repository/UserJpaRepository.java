package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gnoht.tlrl.domain.User;

public interface UserJpaRepository 
		extends JpaRepository<User, Long> {

	public User findOneByName(String name);
	public User findOneByEmail(String email);
}
