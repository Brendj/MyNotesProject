/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.menu;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfMenuExchange;
import ru.axetta.ecafe.processor.core.persistence.MenuExchange;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 14.01.12
 * Time: 19:35
 * To change this template use File | Settings | File Templates.
 */
public class MenuExchangePage extends BasicWorkspacePage {

    private List<MenuExchangeItem> menuExchangeItemList = Collections.emptyList();
    
    public static class MenuExchangeItem{
        private String menuData;
        private int flags;
        private Date menuDate;

        public MenuExchangeItem(String menuData, int flags, Date menuDate) {
            this.menuData = menuData;
            this.flags = flags;
            this.menuDate = menuDate;
        }

        public String getMenuData() {
            return menuData;
        }

        public void setMenuData(String menuData) {
            this.menuData = menuData;
        }

        public int getFlags() {
            return flags;
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }

        public Date getMenuDate() {
            return menuDate;
        }

        public void setMenuDate(Date menuDate) {
            this.menuDate = menuDate;
        }
    }

    public void  fill(Session session, Long idOfOrg) throws Exception{
        List<MenuExchangeItem> items = new LinkedList<MenuExchangeItem>();
        Criteria menuExchangeCriteria = session.createCriteria(MenuExchange.class);
        menuExchangeCriteria.add(
                Restrictions.and(
                        Restrictions.eq("compositeIdOfMenuExchange.idOfOrg", idOfOrg),
                        Restrictions.ne("flags",1)
                )
        );
        List<MenuExchange> menuExchanges = menuExchangeCriteria.list();
        for(MenuExchange menuExchange: menuExchanges)   {
            items.add(new MenuExchangeItem(menuExchange.getMenuData(), menuExchange.getFlags(), menuExchange.getCompositeIdOfMenuExchange().getMenuDate()));
        }
        this.menuExchangeItemList=items;
    }

    public List<MenuExchangeItem> getMenuExchangeItemList() {
        return menuExchangeItemList;
    }

    public String getPageFilename() {
        return "org/menu/exchange";
    }

}
