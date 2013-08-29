/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.feed;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.08.13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class FeedPlanPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(FeedPlanPage.class);

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;
    private Org org;
    private String errorMessages;
    private String infoMessages;




    /**
     * ****************************************************************************************************************
     * Загрузка данных из БД
     * ****************************************************************************************************************
     */
    @Transactional
    public Org getOrg() {
        if (org != null) {
            return org;
        }
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            return getOrg(session);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Org getOrg(Session session) {
        if (org != null) {
            return org;
        }
        org = (Org) session.get(Org.class, 0L);
        return org;
    }

    @Transactional
    public void fill() {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session);
        } catch (Exception e) {
            logger.error("Failed to load discounts data", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session) throws Exception {
        String sql = "select cf_clientgroups.idofclientgroup, cf_clientgroups.groupname, cf_clients.idofclient, cf_persons.firstname, "
                + "       cf_persons.secondname, cf_persons.surname, cf_clientscomplexdiscounts.idofrule, description, idofcomplex, cf_discountrules.priority "
                + "from cf_clients "
                + "join cf_clientscomplexdiscounts on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                + "left join cf_persons on cf_clients.idofperson=cf_persons.idofperson "
                + "left join cf_clientgroups on cf_clients.idofclientgroup=cf_clientgroups.idofclientgroup and cf_clients.idoforg=cf_clientgroups.idoforg "
                + "left join cf_discountrules on cf_discountrules.idofrule=cf_clientscomplexdiscounts.idofrule "
                + "where cf_clients.idoforg=:idoforg "
                + "order by cf_clients.idofclient, cf_discountrules.priority";
        session.createSQLQuery(sql);
    }








    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(FeedPlanPage.class).fill();
    }






    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "feed/feed_plan";
    }

    public String getPageTitle() {
        return "План питания";
    }

    public void resetMessages() {
        errorMessages = "";
        infoMessages = "";
    }

    public void sendError(String message) {
        errorMessages = message;
    }

    public void sendInfo(String message) {
        infoMessages = message;
    }

    public String getInfoMessages() {
        return infoMessages;
    }

    public String getErrorMessages() {
        return errorMessages;
    }
}
