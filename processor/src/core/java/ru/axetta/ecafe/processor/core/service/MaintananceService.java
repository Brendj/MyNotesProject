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
    private Integer maintananceHour;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RuntimeContext runtimeContext;


    @Transactional
    public void run() {
        if (!RuntimeContext.getInstance().isMainNode()) return;
        ////
        if (maintananceHour==null) {
            maintananceHour = runtimeContext.getPropertiesValue(RuntimeContext.PARAM_NAME_DB_MAINTANANCE_HOUR, 22);
            logger.info("DB maintanance hour: "+maintananceHour+", current hour: "+Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }
        if (!runtimeContext.getOptionValueBool(Option.OPTION_CLEAN_MENU)) return;
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)==maintananceHour && (lastCleanDate==null || System.currentTimeMillis()-lastCleanDate.getTime()>12*60*60*1000)) {
            logger.info("Starting DB maintanance procedures...");
            lastCleanDate = new Date();
            try {
                String report = clean();
                logger.info("DB maintanance procedures finished successfully. " + report);
            } catch (Exception e) {
                logger.error("Database cleaning failed", e);
            }
        }
    }

    public String clean() throws Exception {
        int menuDaysForDeletion = runtimeContext.getOptionValueInt(Option.OPTION_MENU_DAYS_FOR_DELETION);
        if (menuDaysForDeletion<0) menuDaysForDeletion=1;

        int menuDeletedCount = 0;
        int menuDetailDeletedCount = 0;
        int menuExchangeDeletedCount = 0;

        logger.info("Cleaning menu details...");
        Query query = entityManager.createNativeQuery("delete from CF_MenuDetails md where md.IdOfMenu in (select m.IdOfMenu from CF_Menu m where m.MenuDate < :date)");
        long timeToClean = System.currentTimeMillis()-(long)menuDaysForDeletion*24*60*60*1000;
        query.setParameter("date", timeToClean);
        menuDetailDeletedCount = query.executeUpdate();

        logger.info("Cleaning menu...");
        query = entityManager.createNativeQuery("delete from CF_Menu m where m.MenuDate < :date");
        query.setParameter("date", timeToClean);
        menuDeletedCount = query.executeUpdate();

        logger.info("Cleaning menu exchange...");
        query = entityManager.createNativeQuery("delete from CF_MenuExchange me where me.MenuDate < :date and me.menuDate<>:nullDate");
        query.setParameter("date", timeToClean);
        // исключаем меню с секцией Settings, у него нулевая дата
        query.setParameter("nullDate", 0);
        menuExchangeDeletedCount = query.executeUpdate();

        return ("Deleted all records before - " + new Date(timeToClean) + ", deleted records count: menu - " + menuDeletedCount +
                ", menu detail - " + menuDetailDeletedCount
                + ", menu exchange - " + menuExchangeDeletedCount);
    }

}
