package com.gnoht.tlrl.service.support;

import java.io.Serializable;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.support.Manageable;

public abstract class ManagedService<ID extends Serializable, 
	T extends Manageable<ID>, R extends PagingAndSortingRepository<T, ID>>
		implements ManageableService<ID, T> {

	protected MessageSourceAccessor messageSource;
	private R repository;
	
	public ManagedService(R repository, MessageSourceAccessor messageSource) {
		this.repository = repository;
		this.messageSource = messageSource;
	}
	
	@Override
	public Long count() {
		return repository.count();
	}

	@Transactional
	@Override
	public T create(T manageable) {
		return repository.save(manageable);
	}

	@Transactional
	@Override
	public void delete(ID id) throws ManageableNotFoundException {
		try {
			repository.delete(id);
		} catch(EmptyResultDataAccessException e) {
			throw new ManageableNotFoundException(id);
		}
	}

	@Transactional
	@Override
	public T save(T manageable) {
		// TODO: unique violation
		return repository.save(manageable);
	}

	@Transactional
	@Override
	public T update(T updatedManageable) throws ManageableNotFoundException {
		//TODO: improve this
		//T manageable = get(updatedManageable.getId());
		return repository.save(updatedManageable);
	}

	@Override
	public T findById(ID id) {
		return repository.findOne(id);
	}

	@Override
	public T get(ID id) throws ManageableNotFoundException {
		T manageable = repository.findOne(id);
		if(manageable == null) {
			throw new ManageableNotFoundException(id);
		}
		return manageable;
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	@Transactional
	@Override
	public void deleteAll() {
		repository.deleteAll();
	}

	protected MessageSourceAccessor getMessageSource() {
		return messageSource;
	}

	protected R getRepository() {
		return repository;
	}
}
