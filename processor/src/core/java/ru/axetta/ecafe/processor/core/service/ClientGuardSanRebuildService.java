/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */
package ru.axetta.ecafe.processor.core.service;

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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 03.07.13
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class ClientGuardSanRebuildService {
    public static final boolean DEBUG_MODE = false;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(BIDataExportService.class);
    public static final String CLEAR_TABLE_SQL = "delete from CF_GuardSan";
    public static final String LOAD_DATA_SQL = "select idofclient, guardsan from CF_Clients where guardsan<>'' order by idofclient";
    public static final String INSERT_DATA_SQL = "insert CF_GuardSan (IdOfClient, GuardSan) VALUES (:idOfClient, :guardSan)";
    public static final String DELIMETER_1 = ",";
    public static final String DELIMETER_2 = ";";

    @PersistenceContext
    EntityManager em;


    public static final String clearGuardSan (String guardSan) {
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
            org.hibernate.Query clear = session.createSQLQuery(CLEAR_TABLE_SQL);
            clear.executeUpdate();
            log("Таблица CF_Client_GuardSan очищена");

            //  Загружаем данные из cf_clients
            Map<Long, String> data = new HashMap<Long, String>();
            org.hibernate.Query select = session.createSQLQuery(LOAD_DATA_SQL);
            List resultList = select.list();
            for (Object entry : resultList) {
                Object e[] = (Object[]) entry;
                long idOfClient= ((BigInteger) e[0]).longValue();
                String guardSan = e[1].toString ();
                data.put(idOfClient, guardSan);
            }

            //  Заполняем cf_client_guardsan
            for (Long idOfClient : data.keySet()) {
                addGuardSan (idOfClient, data.get(idOfClient), session);
            }
        } catch (Exception e) {
            logger.error("Failed to update CF_Client_GuardSan table", e);
        }
    }


    public static void addGuardSan (long idOfClient, String guardSan, Session session) throws Exception {
        Client cl = DAOUtils.findClient(session, idOfClient);
        addGuardSan (cl, guardSan, session);
    }


    public static void addGuardSan (Client cl, String guardSan, Session session) throws Exception {
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
                GuardSan newGuardSan = new GuardSan(cl, i);
                newGuardSan.setGuardSan(i);
                session.save(newGuardSan);
            }
        } else {
            guardSan = clearGuardSan(guardSan);
            if (guardSan.length() < 1) {
                return;
            }
            GuardSan newGuardSan = new GuardSan(cl, guardSan);
            newGuardSan.setGuardSan(guardSan);
            session.save(newGuardSan);
        }
    }


    public void log (String str) {
        if (DEBUG_MODE) {
            logger.info(str);
        }
    }
}