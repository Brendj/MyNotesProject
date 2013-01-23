/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.01.13
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDAOService {

    private Session session;

    public Session getSession() {
        if(!session.isOpen()){
            session = session.getSessionFactory().openSession();
        }
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

}
