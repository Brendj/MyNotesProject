/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.claim;

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
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 15.08.13
 * Time: 15:33
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ClaimCalendarEditPage extends BasicWorkspacePage {


    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ClaimCalendarEditPage.class);
    private String errorMessages;
    private String infoMessages;
    private Org org;




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
    public void fill(boolean reset) {
        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            fill(session, reset);
        } catch (Exception e) {
            logger.error("Failed to load client by name", e);
            sendError("Произошел критический сбой, пожалуйста, повторите попытку позже");
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void fill(Session session, boolean reset) throws Exception {
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */





    /**
     * ****************************************************************************************************************
     * Работа с данными
     * ****************************************************************************************************************
     */








    /**
     * ****************************************************************************************************************
     * Вспомогательные методы и классы
     * ****************************************************************************************************************
     */
    public String getPageFilename() {
        return "claim/claim_calendar";
    }

    public String getPageTitle() {
        return "Регистрация клиентов";
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


    public static class Claim {
        private Date date;
        private String feed;
        private double value;

        public Claim (Date date, String feed, double value) {
            this.date = date;
            this.feed = feed;
            this.value = value;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getFeed() {
            return feed;
        }

        public void setFeed(String feed) {
            this.feed = feed;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}