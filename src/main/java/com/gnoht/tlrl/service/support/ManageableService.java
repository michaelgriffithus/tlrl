package com.gnoht.tlrl.service.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gnoht.tlrl.domain.ManageableNotFoundException;
import com.gnoht.tlrl.domain.support.Manageable;

/**
 * Interface defining service level support for a {@link Manageable}.
 * 
 * @param <ID> Identifier of Manageable.
 * @param <T> type of Manageable being supported.
 */
public interface ManageableService<ID extends Serializable, T extends Manageable<ID>> {
	
	/**
	 * @return the count of entities in this repository.
	 */
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
	 * @return ID of deleted entity.
	 * @throws ManageableNotFoundException if not resource if found with given id.
	 */
	public ID delete(ID id) throws ManageableNotFoundException;
	
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
	 * Finds all entities.
	 * 
	 * @return Collection of found entities or empty collection.
	 */
	public List<T> findAll();
	
	/**
	 * Deletes all entities.
	 */
	public void deleteAll();


}
