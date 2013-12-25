/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;

@Component
@Scope("singleton")
public class MaintenanceService {

    private Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    private Date lastCleanDate;
    private Date srcOrgLastCleanDate;
    private Integer maintananceHour;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    @Autowired
    private RuntimeContext runtimeContext;

    private MaintenanceService getProxy() {
        return RuntimeContext.getAppContext().getBean(MaintenanceService.class);
    }

    public void run() {
        if (!RuntimeContext.getInstance().isMainNode()) {
            return;
        }
        if (!runtimeContext.getOptionValueBool(Option.OPTION_CLEAN_MENU)) {
            return;
        }
        if (maintananceHour == null) {
            maintananceHour = runtimeContext.getPropertiesValue(RuntimeContext.PARAM_NAME_DB_MAINTANANCE_HOUR, 22);
            logger.info("DB maintanance hour: " + maintananceHour + ", current hour: " + Calendar.getInstance()
                    .get(Calendar.HOUR_OF_DAY));
        }
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == maintananceHour) {
            if (lastCleanDate == null || System.currentTimeMillis() - lastCleanDate.getTime() > 12 * 60 * 60 * 1000) {
                logger.info("Starting DB maintanance procedures...");
                lastCleanDate = new Date();
                try {
                    long duration = System.currentTimeMillis();
                    String report = getProxy().clean(false);
                    duration = System.currentTimeMillis() - duration;
                    logger.debug("Total duration = " + duration);
                    logger.info("DB maintanance procedures finished successfully. " + report);
                } catch (Exception e) {
                    logger.error("Database cleaning failed", e);
                }
            }
            // Очистка организаций-поставщиков.
            if (srcOrgLastCleanDate == null || System.currentTimeMillis() - srcOrgLastCleanDate.getTime() > 12 * 60 * 60 * 1000) {
                logger.info("Starting DB maintanance procedures: source organizations...");
                srcOrgLastCleanDate = new Date();
                try {
                    String report = getProxy().clean(true);
                    logger.info("DB maintanance procedures finished successfully. " + report);
                } catch (Exception e) {
                    logger.error("Database cleaning failed", e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.SUPPORTS, readOnly = true)
    public String clean(boolean isSource) throws Exception {
        logger.debug("start clean: "+ new Date());
        long menuDaysForDeletion = runtimeContext.getOptionValueInt(
                isSource ? Option.OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION : Option.OPTION_MENU_DAYS_FOR_DELETION);
        if (menuDaysForDeletion < 0) {
            menuDaysForDeletion = 1;
        }
        long timeToClean = System.currentTimeMillis() - menuDaysForDeletion * 24 * 60 * 60 * 1000;

        String orgFilter = (isSource ? "" : "not") + " in (select distinct mer.idOfSourceOrg from CF_MenuExchangeRules mer)";

        Query query = entityManager.createNativeQuery(
                "select m.IdOfMenu, m.IdOfOrg from CF_Menu m where m.IdOfOrg " + orgFilter + " and m.MenuDate < :date")
                .setParameter("date", timeToClean);
        List<Object[]> records = query.getResultList();
        logger.debug("count menu: "+ records.size());
        Set<Long> orgIds = new HashSet<Long>();
        MaintenanceService proxy = getProxy();

        logger.info("Cleaning menu details and menu...");
        int menuDetailDeletedCount = 0;
        int menuDeletedCount = 0;
        for (Object[] row : records) {
            Long idOfMenu = ((BigInteger) row[0]).longValue();
            orgIds.add(((BigInteger) row[1]).longValue());
            long duration = System.currentTimeMillis();
            logger.debug("Clean menu details from Menu "+idOfMenu);
            int[] res = proxy.cleanMenuInternal(idOfMenu);
            duration = System.currentTimeMillis()-duration;
            logger.debug("Successfully delete menu details duration= "+duration);
            menuDetailDeletedCount += res[0];
            menuDeletedCount += res[1];
        }

        logger.info("Cleaning menu exchange...");
        int menuExchangeDeletedCount = 0;
        for (Long idOfOrg : orgIds) {
            menuExchangeDeletedCount += proxy.cleanMenuExchange(idOfOrg, timeToClean);
        }

        return ("Deleted all records before - " + new Date(timeToClean) + ", deleted records count: menu - "
                + menuDeletedCount +
                ", menu detail - " + menuDetailDeletedCount + ", menu exchange - " + menuExchangeDeletedCount);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int[] cleanMenuInternal(Long idOfMenu) {
        int[] res = new int[2];
        //String onHashJoin = "set enable_nestloop = off;set enable_hashjoin = on;set enable_mergejoin = off;";
        //Query onHashJoinQuery =  entityManager.createNativeQuery(onHashJoin);
        //onHashJoinQuery.executeUpdate();
        Query detailsQuery = entityManager
                .createNativeQuery("DELETE FROM CF_MenuDetails md WHERE md.IdOfMenu = :idOfMenu");
        Query menuQuery = entityManager.createNativeQuery("DELETE FROM CF_Menu m WHERE m.IdOfMenu = :idOfMenu");
        detailsQuery.setParameter("idOfMenu", idOfMenu);
        res[0] = detailsQuery.executeUpdate();
        menuQuery.setParameter("idOfMenu", idOfMenu);
        res[1] = menuQuery.executeUpdate();
        //String onAllOptions = "set enable_nestloop = on;set enable_hashjoin = on;set enable_mergejoin = on;";
        //Query onAllOptionsQuery =  entityManager.createNativeQuery(onAllOptions);
        //onAllOptionsQuery.executeUpdate();
        return res;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public int cleanMenuExchange(Long idOfOrg, long timeToClean) {
        Query query = entityManager.createNativeQuery("DELETE FROM CF_MenuExchange me WHERE me.IdOfOrg = :idOfOrg"
                + " AND me.MenuDate < :date AND me.menuDate <> :nullDate");
        query.setParameter("idOfOrg", idOfOrg).setParameter("date", timeToClean).setParameter("nullDate", 0);
        return query.executeUpdate();
    }
}
