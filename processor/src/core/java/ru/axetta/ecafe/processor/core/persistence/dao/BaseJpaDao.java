/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: shamil
 * Date: 10.10.14
 * Time: 10:59
 */
public abstract class BaseJpaDao {
    @PersistenceContext(unitName = "reportsPU")
    protected EntityManager entityManager;
}
