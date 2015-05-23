package com.gnoht.tlrl.repository;

import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
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
public abstract class ManageablePostInsertEventListener<T> 
		implements PostCommitInsertEventListener, PostInsertEventListener {

	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOG = LoggerFactory.getLogger(ManageablePostInsertEventListener.class);

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
	@Override @SuppressWarnings("unchecked") 
	public final void onPostInsert(PostInsertEvent event) {
		if(event.getEntity().getClass().isAssignableFrom(getSupportedClass())) {
			handleSuccess((T) event.getEntity(), event);
		}
	}

	/**
	 * Delegates handling of failed insert to subclasses, first checking to 
	 * see if the entity is supported by this listener. 
	 * 
	 * @see ManageablePostInsertEventListener#getSupportedClass()
	 * @see PostCommitInsertEventListener#onPostInsertCommitFailed(PostInsertEvent)
	 */
	@Override @SuppressWarnings("unchecked") 
	public final void onPostInsertCommitFailed(PostInsertEvent event) {
		if(event.getEntity().getClass().isAssignableFrom(getSupportedClass())) {
			handleFailure((T) event.getEntity(), event);
		}
	}

	/**
	 * Handler called after a successful insert and check for supported entity.
	 * 
	 * @param entity {@link Manageable} that was inserted
	 * @param event {@link PostInsertEvent} with details of the insert, and context.
	 */
	public abstract void handleSuccess(T entity, PostInsertEvent event);
	
	/**
	 * 
	 * @param entity {@link Manageable} that failed insert
	 * @param event {@link PostInsertEvent} with details of the insert, and context.
	 */
	public abstract void handleFailure(T entity, PostInsertEvent event);
	
	/**
	 * The {@link Manageable} that sub-classing listeners will support.
	 * @return 
	 */
	abstract Class<T> getSupportedClass();
}
