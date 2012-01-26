/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.cleaner;

import ru.axetta.ecafe.processor.core.persistence.Menu;
import ru.axetta.ecafe.processor.core.persistence.MenuDetail;
import ru.axetta.ecafe.processor.core.persistence.MenuExchange;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

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

    public static void clean(Session session) throws Exception {
        Criteria optCriteria = session.createCriteria(Option.class);
        optCriteria.add(Restrictions.eq("idOfOption", 4L));
        cleanMenuOption = (Option) optCriteria.uniqueResult();
        cleanMenu = cleanMenuOption.getOptionText().equals("1");
        optCriteria = session.createCriteria(Option.class);
        optCriteria.add(Restrictions.eq("idOfOption", 5L));
        menuDateForDeletionOption = (Option)optCriteria.uniqueResult();
        menuDateForDeletion = new Date(Long.parseLong(menuDateForDeletionOption.getOptionText()));

        if (cleanMenu) {
            Criteria menuCriteria = session.createCriteria(Menu.class);
            menuCriteria.add(Restrictions.lt("menuDate", menuDateForDeletion));
            List<Menu> menuList = menuCriteria.list();
            for (Menu menu : menuList) {
                Criteria menuDetailCriteria = session.createCriteria(MenuDetail.class);
                menuDetailCriteria.add(Restrictions.eq("menu", menu));
                List<MenuDetail> menuDetailList = menuDetailCriteria.list();                    
                for (MenuDetail menuDetail : menuDetailList)
                    session.delete(menuDetail);
                session.delete(menu);
            }
            Criteria menuExchangeCriteria = session.createCriteria(MenuExchange.class);
            menuExchangeCriteria.add(Restrictions.lt("compositeIdOfMenuExchange.menuDate", menuDateForDeletion));
            List<MenuExchange> menuExchangeList = menuExchangeCriteria.list();
            for (MenuExchange menuExchange : menuExchangeList)
                session.delete(menuExchange);
        }
    }
}
