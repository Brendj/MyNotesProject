/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.08.12
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractEntityItem<E> {

    protected EntityManager entityManager;

    public abstract void fill(E entity);
    public abstract Class<E> getEntity();

    public AbstractEntityItem(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
