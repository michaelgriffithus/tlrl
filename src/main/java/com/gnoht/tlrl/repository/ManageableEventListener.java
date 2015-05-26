package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostCommitDeleteEventListener;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gnoht.tlrl.domain.support.Manageable;

/**
 * Abstract {@link PostInsertEventListener} implementation with provides check
 * for supported entity handling.
 * 
 * @param <T> Type of supported entity class.
 */
public abstract class ManageableEventListener<T> 
		implements PostCommitInsertEventListener, PostInsertEventListener, 
			PostCommitUpdateEventListener, PostUpdateEventListener,
			PostCommitDeleteEventListener, PostDeleteEventListener {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOG = LoggerFactory.getLogger(ManageableEventListener.class);

	@Override
	public boolean requiresPostCommitHanding(EntityPersister persister) {
		/* TODO: not sure why but regardless of the persister.getMappedClass()
		 * it always calls the all the PostInsertListeners, so we've moved
		 * the specific mappedClass == supportedClass check to onPostInsert method.*/
		return true;
	}

	/**
	 * Delegates handling of a successful insert to subclasses, first checking
	 * to see if the inserted entity is supported by this listener.
	 * 
	 * @see org.hibernate.event.spi.PostInsertEventListener#onPostInsert(
	 * 	org.hibernate.event.spi.PostInsertEvent)
	 */
	//@Override @SuppressWarnings("unchecked") 
	public final void onPostInsert(PostInsertEvent event) {
		if(isEntityAssignableFromSupportedClass(event.getEntity())) {
			handlePostInsertSuccess((T) event.getEntity(), event);
		}
	}

	/**
	 * Delegates handling of failed insert to subclasses, first checking to 
	 * see if the entity is supported by this listener. 
	 * 
	 * @see ManageableEventListener#getSupportedClass()
	 * @see PostCommitInsertEventListener#onPostInsertCommitFailed(PostInsertEvent)
	 */
	//@Override @SuppressWarnings("unchecked") 
	public final void onPostInsertCommitFailed(PostInsertEvent event) {
		if(isEntityAssignableFromSupportedClass(event.getEntity())) {
			handlePostInsertFailure((T) event.getEntity(), event);
		}
	}

	/**
	 * Delegates handling of successful update to subclasses, first checking to
	 * see if the entity is supported by this listener.
	 * 
	 * @see ManageableEventListener#getSupportedClass()
	 * @see PostUpdateEventListener#onPostUpdate(PostUpdateEvent)	 
	 */
	@Override @SuppressWarnings("unchecked")
	public void onPostUpdate(PostUpdateEvent event) {
		if(isEntityAssignableFromSupportedClass(event.getEntity())) {
			handlePostUpdateSuccess((T) event.getEntity(), event);
		}
	}

	/**
	 * Delegates handling of failed update to subclasses, first checking to 
	 * see if the entity is supported by this listener.
	 * 
	 * @see ManageableEventListener#getSupportedClass()
	 * @see PostCommitUpdateEventListener#onPostUpdateCommitFailed(PostUpdateEvent)	 
	 */
	@Override @SuppressWarnings("unchecked")
	public void onPostUpdateCommitFailed(PostUpdateEvent event) {
		if(isEntityAssignableFromSupportedClass(event.getEntity()))
			handlePostUpdateFailure((T) event.getEntity(), event);
	}
	
	/**
	 * Delegates handling of successful delete to subclasses, first checking to 
	 * see if the entity is supported by this listener.
	 * 
	 * @see ManageableEventListener#getSupportedClass()
	 * @see PostDeleteEventListener#onPostDelete(PostDeleteEvent)	 
	 */
	@Override @SuppressWarnings("unchecked")
	public void onPostDelete(PostDeleteEvent event) {
		if(isEntityAssignableFromSupportedClass(event.getEntity()))
			handlePostDeleteSuccess((T) event.getEntity(), event);
	}

	/**
	 * Delegates handling of failed delete to subclasses, first checking to 
	 * see if the entity is supported by this listener.
	 * 
	 * @see ManageableEventListener#getSupportedClass()
	 * @see PostCommitDeleteEventListener#onPostDeleteCommitFailed(PostDeleteEvent)	 
	 */
	@Override @SuppressWarnings("unchecked")
	public void onPostDeleteCommitFailed(PostDeleteEvent event) {
		if(isEntityAssignableFromSupportedClass(event.getEntity()))
			handlePostDeleteFailure((T) event.getEntity(), event);
	}

	/**
	 * Checks if given entity is supported by this event listener.
	 * @param entity
	 * @return
	 */
	boolean isEntityAssignableFromSupportedClass(Object entity) {
		return entity.getClass().isAssignableFrom(getSupportedClass());
	}
	
	/**
	 * Handler called after a successful insert for supported entity.
	 * 
	 * @param entity {@link Manageable} that was inserted
	 * @param event {@link PostInsertEvent} with details of the insert, and context.
	 */
	public abstract void handlePostInsertSuccess(T entity, PostInsertEvent event);
	
	/**
	 * Handler called after a failed insert for supported entity.
	 * 
	 * @param entity {@link Manageable} that failed insert
	 * @param event {@link PostInsertEvent} with details of the insert, and context.
	 */
	public abstract void handlePostInsertFailure(T entity, PostInsertEvent event);
	
	/**
	 * Handler called after a successful update for supported entity.
	 * 
	 * @param entity {@link Manageable} that was updated
	 * @param event {@link PostUpdateEvent} with details of the update, and context.
	 */
	public abstract void handlePostUpdateSuccess(T entity, PostUpdateEvent event);
	
	/**
	 * Handler called after a failed update for supported entity.
	 * 
	 * @param entity {@link Manageable} that was updated
	 * @param event {@link PostUpdateEvent} with details of the update, and context.
	 */
	public abstract void handlePostUpdateFailure(T entity, PostUpdateEvent event);
	
	/**
	 * Handler called after a successful delete for supported entity.
	 * 
	 * @param entity {@link Manageable} that was updated
	 * @param event {@link PostDeleteEvent} with details of the update, and context.
	 */
	public abstract void handlePostDeleteSuccess(T entity, PostDeleteEvent event);
	
	/**
	 * Handler called after a failed delete for supported entity.
	 * 
	 * @param entity {@link Manageable} that was updated
	 * @param event {@link PostDeleteEvent} with details of the update, and context.
	 */
	public abstract void handlePostDeleteFailure(T entity, PostDeleteEvent event);
	
	/**
	 * The {@link Manageable} that sub-classing listeners will support.
	 * @return 
	 */
	abstract Class<T> getSupportedClass();
}
