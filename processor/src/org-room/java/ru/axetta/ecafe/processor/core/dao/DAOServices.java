/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.dao;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 07.08.13
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
@Transactional
public class DAOServices {
    private final static Logger logger = LoggerFactory.getLogger(DAOServices.class);

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * ****************************************************************************************************************
     * SQL
     * ****************************************************************************************************************
     */
    public static final String SQL_LOAD_GROUPS = "select idofclientgroup, groupname from cf_clientgroups where idoforg=:idoforg order by groupname";




    public static DAOServices getInstance() {
        return RuntimeContext.getAppContext().getBean(DAOServices.class);
    }




    /**
     * ****************************************************************************************************************
     * Методы с использованием EntityManager
     * ****************************************************************************************************************
     */





    /**
     * ****************************************************************************************************************
     * Метода с использование Session
     * ****************************************************************************************************************
     */
    public List<String> loadGroups(Session session, long idoforg) {
        List<String> groups = new ArrayList<String>();
        org.hibernate.Query q = session.createSQLQuery(SQL_LOAD_GROUPS);
        q.setLong("idoforg", idoforg);
        List resultList = q.list();
        groups.add("");
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idOfClientGroup = HibernateUtils.getDbLong(o[0]);
            String groupName = HibernateUtils.getDbString(o[1]);
            groups.add(groupName);
        }
        return groups;
    }
}
