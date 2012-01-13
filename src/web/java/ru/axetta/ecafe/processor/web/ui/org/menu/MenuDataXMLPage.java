/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.menu;

import ru.axetta.ecafe.processor.core.persistence.MenuExchange;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 13.01.12
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class MenuDataXMLPage extends BasicWorkspacePage {
    private Long idOfOrg;
    private List<Item> items = Collections.emptyList();
    public static class Item{
        private String menuData;
        private int flags;

        public Item(String menuData, int flags) {
            this.menuData = menuData;
            this.flags = flags;
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

        public String getPageFilename() {
            return "org/menu/exchange";
        }

        public void setFlags(int flags) {
            this.flags = flags;
        }
    }

    public void buildListMenuData(Session session, Long idOfOrg) throws Exception  {
        List<Item> items = new LinkedList<Item>();
        this.idOfOrg=idOfOrg;
        Criteria criteria = session.createCriteria(MenuExchange.class);
        criteria.add(Restrictions.eq("compositeIdOfMenuExchange.idOfOrg", idOfOrg));
        List menies = criteria.list();
        for (Object object : menies) {
            MenuExchange menuExchange = (MenuExchange) object;
            items.add(new Item(menuExchange.getMenuData(),menuExchange.getFlags())) ;
        }
        this.items=items;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public List<Item> getItems() {
        return items;
    }

}
