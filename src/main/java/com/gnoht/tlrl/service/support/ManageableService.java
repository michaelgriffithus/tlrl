package com.gnoht.tlrl.service.support;

import java.io.Serializable;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.support.Manageable;

public interface ManageableService<ID extends Serializable, T extends Manageable<ID>> {
	
	public Long count();
	
	/**
	 * Creates a new entity.
	 * @param entity The entity to create.
	 * @return The created entity.
	 */
	public T create(T manageable);
	
	/**
	 * Deletes a entity.
	 * @param id The id of entity to delete.
	 * @return The deleted entity.
	 * @throws EntityNotFoundException if no entity is found with given id. 
	 */
	/**
	 * Deletes a Resource
	 * @param id The id of resource to delete.
	 */
	public void delete(ID id);
	
	/**
	 * Convenience method for saving - either via "create" for new, or
	 * update for existing. Takes care of determining if entity exists. 
	 * @param entity
	 * @return
	 */
	public T save(T manageable);
	
	/**
	 * Updates a entity.
	 * @param entity The entity to update.
	 * @return The updated entity.
	 * @throws EntityNotFoundException if no entity is found with given id.
	 */
	public T update(T manageable) throws ManageableNotFoundException;
	
	/**
	 * Finds a entity.
	 * @param id The id of entity to find.
	 * @return The found entity, otherwise null.
	 */
	public T findById(ID id);
	
	/**
	 * Gets the entity associated with given id. Assumes entity exists, otherwise
	 * throws an {@link EntityNotFoundException}
	 * @param id
	 * @return
	 * @throws EntityNotFoundException if no entity is found with given id.
	 */
	public T get(ID id) throws ManageableNotFoundException;
	
	/**
	 * Finds all entities using {@link Pageable} attributes.
	 * @return A {@link Page} containing list of entities and page attributes.
	 */
	public Page<T> findAll(Pageable pageable);
	
	/**
	 * Deletes all entities.
	 */
	public void deleteAll();

}
