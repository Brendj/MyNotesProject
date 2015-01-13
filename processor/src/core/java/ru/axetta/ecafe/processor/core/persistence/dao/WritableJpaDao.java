/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * User: shamil
 * Date: 10.01.15
 * Time: 10:59
 */
public abstract class WritableJpaDao {
    @PersistenceContext(unitName = "processorPU")
    protected EntityManager entityManager;
}
