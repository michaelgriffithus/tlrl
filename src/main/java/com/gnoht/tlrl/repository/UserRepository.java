package com.gnoht.tlrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import com.gnoht.tlrl.domain.User;

/**
 * {@link Repository} interface for {@link User}s.
 */
public interface UserRepository 
		extends JpaRepository<User, Long>{
	
	public User findOneByName(String name);
	public User findOneByEmail(String email);
}
