/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.abstractpage;

import org.hibernate.Criteria;

import javax.persistence.EntityManager;

/* Класс фильтрации сущностей */
public abstract class AbstractFilter<E> {

    public abstract boolean isEmpty();
    public abstract void clear();
    protected abstract void apply(EntityManager entityManager, Criteria crit);

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

}
