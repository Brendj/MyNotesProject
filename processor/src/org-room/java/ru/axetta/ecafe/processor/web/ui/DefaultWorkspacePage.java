/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.07.13
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class DefaultWorkspacePage extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    private String org;
    private Map<Long, String> orgs;
    

    @Transactional
    public void fill() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load orgs", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session) throws Exception {
        orgs = new TreeMap<Long, String>();
        String sql = "select cf_orgs.idoforg, officialname, count(cf_clients.idofclient) "
                    + "from cf_orgs "
                    + "left join cf_clients on cf_clients.idoforg=cf_orgs.idoforg "
                    + "group by cf_orgs.idoforg, officialname "
                    + "having count(cf_clients.idofclient)>2";
        org.hibernate.Query q = session.createSQLQuery(sql);
        List resultList = q.list();
        for (Object entry : resultList) {
            Object o[] = (Object[]) entry;
            orgs.put(HibernateUtils.getDbLong(o[0]), HibernateUtils.getDbString(o[1]));
        }
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(DefaultWorkspacePage.class).fill();
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public List<SelectItem> getOrgs() throws Exception {
        if (orgs == null) {
            RuntimeContext.getAppContext().getBean(DefaultWorkspacePage.class).fill();
        }
        List<SelectItem> res = new ArrayList<SelectItem>();
        for (Long idoforg : orgs.keySet()) {
            res.add(new SelectItem(orgs.get(idoforg), orgs.get(idoforg)));
        }
        return res;
    }

    public void doApply () {
        long idoforg = 0;
        for (Long id : orgs.keySet()) {
            String n = orgs.get(id);
            if (n.equals(org)) {
                idoforg = id;
            }
        }
        MainPage.getSessionInstance().setIdoforg(idoforg);
    }

    public String getPageTitle() {
        return "Добро пожаловать!";
    }
}