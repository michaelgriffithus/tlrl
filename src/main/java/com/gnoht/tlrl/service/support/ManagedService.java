package com.gnoht.tlrl.service.support;

import java.io.Serializable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.support.Manageable;

/**
 * Generic {@link ManagedService} implementation, providing basic CRUD support 
 * the target {@link Manageable}.
 * 
 * @param <ID> Identifier of Manageable.
 * @param <T> Type of target Manageable.
 * @param <R> {@link Repository} of targeted Manageable.
 */
@Transactional(readOnly=true) // default
public abstract class ManagedService<ID extends Serializable, 
			T extends Manageable<ID>, R extends JpaRepository<T, ID>> 
		implements ManageableService<ID, T> {

	private static final Logger LOG = LoggerFactory.getLogger(ManagedService.class);
	
	protected MessageSourceAccessor messageSource;
	protected R repository;
	
	public ManagedService(R repository, MessageSourceAccessor messageSource) {
		this.repository = repository;
		this.messageSource = messageSource;
	}
	
	@Override
	public Long count() {
		LOG.info("Starting count()");
		return repository.count();
	}

	@Transactional(readOnly=false)
	@Override
	public T create(T manageable) {
		LOG.info("Starting create(): manageable={}", manageable);
		return repository.save(manageable);
	}

	@Transactional(readOnly=false)
	@Override
	public void delete(ID id) throws ManageableNotFoundException {
		LOG.info("Starting delete(): id={}", id);
		assertExistingManageable(id);
		repository.delete(id);
	}

	@Transactional(readOnly=false)
	@Override
	public T save(T manageable) {
		LOG.info("Starting save(): manageable={}", manageable);
		// TODO: unique violation
		return repository.save(manageable);
	}

	/**
	 * Helper for checking if {@link Manageable} exists, throwing proper
	 * exception if not found.
	 * 
	 * @param id Identifier of Manageable.
	 */
	protected void assertExistingManageable(ID id) {
		if(!repository.exists(id)) {
			throw new ManageableNotFoundException(id);
		}
	}
	
	/* Delegates to save method. Subclasses should override this. */
	@Transactional(readOnly=false)
	@Override
	public T update(T updatedManageable) throws ManageableNotFoundException {
		LOG.info("Starting update(): updatedManageable={}", updatedManageable);
		assertExistingManageable(updatedManageable.getId());
		return repository.save(updatedManageable);
	}

	@Override
	public T findById(ID id) {
		LOG.info("Starting findById(): id={}", id);
		return repository.findOne(id);
	}

	@Override
	public T get(ID id) throws ManageableNotFoundException {
		LOG.info("Starting get(): id={}", id);
		T manageable = repository.findOne(id);
		if(manageable == null) {
			throw new ManageableNotFoundException(id);
		}
		return manageable;
	}

	@Override
	public Page<T> findAll(Pageable pageable) {
		LOG.info("Starting findAll(): pageable={}", pageable);
		return repository.findAll(pageable);
	}

	@Transactional(readOnly=false)
	@Override
	public void deleteAll() {
		LOG.info("Starting deleteAll():");
		repository.deleteAll();
	}

	@Override
	public List<T> findAll() {
		LOG.info("Starting findAll():");
		return repository.findAll();
	}

	protected MessageSourceAccessor getMessageSource() {
		return messageSource;
	}
	protected R getRepository() {
		return repository;
	}
}
