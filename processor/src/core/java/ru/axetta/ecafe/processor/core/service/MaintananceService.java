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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;

@Component
@Scope("singleton")
public class MaintananceService {

    private Logger logger = LoggerFactory.getLogger(MaintananceService.class);

    private Date lastCleanDate;
    private Date srcOrgLastCleanDate;
    private Integer maintananceHour;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RuntimeContext runtimeContext;


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
                    String report = clean(false);
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
                    String report = clean(true);
                    logger.info("DB maintanance procedures finished successfully. " + report);
                } catch (Exception e) {
                    logger.error("Database cleaning failed", e);
                }
            }
        }
    }

    @Transactional
    public String clean(boolean isSource) throws Exception {
        long menuDaysForDeletion = runtimeContext.getOptionValueInt(
                isSource ? Option.OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION : Option.OPTION_MENU_DAYS_FOR_DELETION);
        if (menuDaysForDeletion < 0) {
            menuDaysForDeletion = 1;
        }
        long timeToClean = System.currentTimeMillis() - menuDaysForDeletion * 24 * 60 * 60 * 1000;

        String orgFilter = (isSource ? "" : "not") + " in (select distinct mer.idOfSourceOrg from CF_MenuExchangeRules mer)";

        logger.info("Cleaning menu details...");
        Query query = entityManager.createNativeQuery("delete from CF_MenuDetails md where md.IdOfMenu in \n" +
                "(select m.IdOfMenu from CF_Menu m where m.IdOfOrg " + orgFilter + "and m.MenuDate < :date)")
                .setParameter("date", timeToClean);
        int menuDetailDeletedCount = query.executeUpdate();

        logger.info("Cleaning menu...");
        query = entityManager
                .createNativeQuery("delete from CF_Menu m where m.IdOfOrg " + orgFilter + " and m.MenuDate < :date")
                .setParameter("date", timeToClean);
        int menuDeletedCount = query.executeUpdate();

        logger.info("Cleaning menu exchange...");
        query = entityManager.createNativeQuery("delete from CF_MenuExchange me where me.IdOfOrg " + orgFilter
                + " and me.MenuDate < :date and me.menuDate <> :nullDate")
                .setParameter("date", timeToClean).setParameter("nullDate", 0);
        // исключаем меню с секцией Settings, у него нулевая дата
        int menuExchangeDeletedCount = query.executeUpdate();

        return ("Deleted all records before - " + new Date(timeToClean) + ", deleted records count: menu - "
                + menuDeletedCount +
                ", menu detail - " + menuDetailDeletedCount + ", menu exchange - " + menuExchangeDeletedCount);
    }
}
