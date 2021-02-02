/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 19.12.13
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
public class DOVersionRepository {

    public static Long updateClassVersion(String doClass, Session persistenceSession) {
        return DAOUtils.getDistributedObjectVersion(persistenceSession, doClass);
    }

}
