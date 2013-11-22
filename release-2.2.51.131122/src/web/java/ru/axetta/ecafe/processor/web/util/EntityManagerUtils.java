/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 01.02.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class EntityManagerUtils {
    private static final String PERSISTENCE_UNIT_NAME = "processor";

    public static EntityManager createEntityManager() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        return entityManagerFactory.createEntityManager();
    }
}
