/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import javax.persistence.EntityManager;

public abstract class AbstractEntityItem<E> {

    public abstract void fillForList(EntityManager entityManager, E entity);
    protected abstract void fill(EntityManager entityManager, E entity);
    protected abstract void saveTo(EntityManager entityManager, E entity);
    public abstract E getEntity(EntityManager entityManager);
    protected abstract E createEmptyEntity();
    
    public AbstractEntityItem() {
    }

    public void updateEntity(EntityManager entityManager) {
        E e = getEntity(entityManager);
        saveTo(entityManager, e);
        entityManager.persist(e);
        //fill(entityManager, e);
    }

    public void createEntity(EntityManager entityManager) {
        E e = createEmptyEntity();
        saveTo(entityManager, e);
        entityManager.persist(e);
        //fill(entityManager, e);
    }

    public void refreshEntity(EntityManager entityManager) {
        fill(entityManager, getEntity(entityManager));
    }

    public void removeEntity(EntityManager entityManager) throws Exception {
        E entity = getEntity(entityManager);
        if (entity==null) throw new Exception("Объект не найден");
        prepareForEntityRemove(entityManager, entity);
        entityManager.remove(entity);
    }

    protected void prepareForEntityRemove(EntityManager entityManager, E entity) {

    }
}
