/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */
package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.GuardSan;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 03.07.13
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
@Transactional
@Component
@Scope("singleton")
public class ClientGuardSanRebuildService {
    public static final boolean DEBUG_MODE = false;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BIDataExportService.class);
    public static final String DELIMETER_1 = ",";
    public static final String DELIMETER_2 = ";";

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;



    public static ClientGuardSanRebuildService getInstance() {
        return RuntimeContext.getAppContext().getBean(ClientGuardSanRebuildService.class);
    }


    public static String clearGuardSan(String guardSan) {
        guardSan = guardSan.replaceAll("(?![0-9]).", "");
        if (guardSan.length() > 11) {
            guardSan = guardSan.substring(0, 11);
        }
        return guardSan;
    }


    @Transactional
    public void rebuild() {
        Session session = (Session) em.getDelegate();
        try {
            //  Очищаем cf_client_guardsan
            DAOUtils.clearGuardSanTable(session);
            log("Таблица CF_Client_GuardSan очищена");

            //  Загружаем данные из cf_clients
            Map<Long, String> data = DAOUtils.getClientGuardSan_Old(session);

            //  Заполняем cf_client_guardsan
            for (Long idOfClient : data.keySet()) {
                addGuardSan (idOfClient, data.get(idOfClient), session);
            }
        } catch (Exception e) {
            logger.error("Failed to update CF_Client_GuardSan table", e);
        }
    }


    public Set<GuardSan> addGuardSan (long idOfClient, String guardSan) throws Exception {
        Session session = (Session) em.getDelegate();
        Client cl = DAOUtils.findClient(session, idOfClient);
        return addGuardSan (cl, guardSan, session);
    }


    public Set<GuardSan> addGuardSan (Client cl, String guardSan) throws Exception {
        return addGuardSan (cl, guardSan, (Session) em.getDelegate());
    }


    public Set<GuardSan> addGuardSan (long idOfClient, String guardSan, Session session) throws Exception {
        Client cl = DAOUtils.findClient(session, idOfClient);
        return addGuardSan (cl, guardSan, session);
    }


    @Transactional
    public Set<GuardSan> addGuardSan (Client cl, String guardSan, Session session) throws Exception {
        Set<GuardSan> result = new HashSet<GuardSan>();
        String list [] = null;
        if (guardSan.indexOf(DELIMETER_1) > -1) {
            list = guardSan.split(DELIMETER_1);
        } else if (guardSan.indexOf(DELIMETER_2) > -1) {
            list = guardSan.split(DELIMETER_2);
        }

        if (list != null && list.length > 0) {
            for (String i : list) {
                i = clearGuardSan(i);
                if (i.length() < 1) {
                    continue;
                }
                try {
                    GuardSan newGuardSan = new GuardSan(cl, i);
                    session.save(newGuardSan);
                    result.add(newGuardSan);
                } catch (Exception e) {
                    logger.error("Failed to add guard san", e);
                }
            }
        } else {
            guardSan = clearGuardSan(guardSan);
            if (guardSan.length() < 1) {
                return Collections.EMPTY_SET;
            }
            try {
                GuardSan newGuardSan = new GuardSan(cl, guardSan);
                session.save(newGuardSan);
                result.add(newGuardSan);
            } catch (Exception e) {
                logger.error("Failed to add guard san", e);
            }
        }
    return result;
    }


    @Transactional
    public void removeGuardSan (long idOfClient) throws Exception {
        Session session = (Session) em.getDelegate();
        Client cl = DAOUtils.findClient(session, idOfClient);
        DAOUtils.removeGuardSan (session, cl);
    }


    public void log (String str) {
        if (DEBUG_MODE) {
            logger.info(str);
        }
    }
}