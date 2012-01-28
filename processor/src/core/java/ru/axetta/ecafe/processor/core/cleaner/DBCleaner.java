/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.cleaner;

import ru.axetta.ecafe.processor.core.persistence.Option;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 26.01.12
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class DBCleaner {
    private static Option cleanMenuOption;
    private static Option menuDateForDeletionOption;

    private static Boolean cleanMenu;
    private static Date menuDateForDeletion;

    public static String clean(Session session, Logger logger) throws Exception {
        Criteria optCriteria = session.createCriteria(Option.class);
        optCriteria.add(Restrictions.eq("idOfOption", 4L));
        cleanMenuOption = (Option) optCriteria.uniqueResult();
        cleanMenu = cleanMenuOption.getOptionText().equals("1");
        optCriteria = session.createCriteria(Option.class);
        optCriteria.add(Restrictions.eq("idOfOption", 5L));
        menuDateForDeletionOption = (Option)optCriteria.uniqueResult();
        menuDateForDeletion = new Date(Long.parseLong(menuDateForDeletionOption.getOptionText()));

        int menuDeletedCount = 0;
        int menuDetailDeletedCount = 0;
        int menuExchangeDeletedCount = 0;

        if (cleanMenu) {
            logger.info("Cleaning menu details...");
            SQLQuery query = session.createSQLQuery("delete from CF_MenuDetails md where md.IdOfMenu in (select m.IdOfMenu from CF_Menu m where m.MenuDate < :date)");
            query.setLong("date", menuDateForDeletion.getTime());
            menuDetailDeletedCount = query.executeUpdate();

            logger.info("Cleaning menu...");
            query = session.createSQLQuery("delete from CF_Menu m where m.MenuDate < :date");
            query.setLong("date", menuDateForDeletion.getTime());
            menuDeletedCount = query.executeUpdate();

            logger.info("Cleaning menu exchange...");
            query = session.createSQLQuery("delete from CF_MenuExchange me where me.MenuDate < :date");
            query.setLong("date", menuDateForDeletion.getTime());
            menuExchangeDeletedCount = query.executeUpdate();

        }

        return ("Menu up data - " + menuDateForDeletion + ", deleted records count: menu - " + menuDeletedCount +
                ", menu detail - " + menuDetailDeletedCount
                + ", menu exchange - " + menuExchangeDeletedCount);
    }
}
