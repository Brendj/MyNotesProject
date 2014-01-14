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
import java.util.*;

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

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    /**
     * ****************************************************************************************************************
     * SQL
     * ****************************************************************************************************************
     */
    public static final String SQL_LOAD_GROUPS = "select idofclientgroup, groupname from cf_clientgroups where idoforg=:idoforg and groupname<>'' order by groupname";




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
        return loadGroups (session, idoforg, false);
    }

    public List<String> loadGroups(Session session, long idoforg, boolean addAll) {
        List<String> groups = new ArrayList<String>();
        org.hibernate.Query q = session.createSQLQuery(SQL_LOAD_GROUPS);
        q.setLong("idoforg", idoforg);
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idOfClientGroup = HibernateUtils.getDbLong(o[0]);
            String groupName = HibernateUtils.getDbString(o[1]);
            groups.add(groupName);
        }
        if (addAll) {
            groups.add("Все");
        }
        return groups;
    }
    
    public Map<Long, String> loadDiscountCategories (Session session) {
        Map <Long, String> categories = new HashMap <Long, String> ();
        org.hibernate.Query q = session.createSQLQuery("select idofcategorydiscount, categoryname "
                + "from cf_categorydiscounts where idofcategorydiscount>0");
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idofcategorydiscount = HibernateUtils.getDbLong(o[0]);
            String name = HibernateUtils.getDbString(o[1]);
            categories.put(idofcategorydiscount, name);
        }
        return categories;
    }

    public List<String []> loadGoodsGroups(Session session) {
        List<String[]> groups = new ArrayList <String[]> ();
        org.hibernate.Query q = session.createSQLQuery("select idofgoodsgroup, nameofgoodsgroup "
                                                    + "from cf_goods_groups order by nameofgoodsgroup");
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            Long idofcategorydiscount = HibernateUtils.getDbLong(o[0]);
            String name = HibernateUtils.getDbString(o[1]);
            groups.add(new String[] { "" + idofcategorydiscount, name});
        }
        return groups;
    }
}
