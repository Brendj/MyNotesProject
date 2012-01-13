/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.menu;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.sync.SyncResponse;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;

import java.io.InputStream;
import java.util.*;

import static org.hibernate.criterion.Restrictions.in;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 11.01.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class MenuViewPage extends BasicWorkspacePage {

    private Long idOfOrg;
    private List<Item> items = Collections.emptyList();

   


    public static class Item {
        private Long idOfMenu;
        private Date menuDate;
        private Date createTime;
        private Integer menuSource;
        private Integer flag;

        public Item(Long idOfMenu, Date menuDate, Date createTime, Integer menuSource, Integer flag) {
            this.idOfMenu = idOfMenu;
            this.menuDate = menuDate;
            this.createTime = createTime;
            this.menuSource = menuSource;
            this.flag = flag;

        }

        public Integer getFlag() {
            return flag;
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }

        public Long getIdOfMenu() {
            return idOfMenu;
        }

        public void setIdOfMenu(Long idOfMenu) {
            this.idOfMenu = idOfMenu;
        }

        public Date getMenuDate() {
            return menuDate;
        }

        public void setMenuDate(Date menuDate) {
            this.menuDate = menuDate;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }

        public Integer getMenuSource() {
            return menuSource;
        }

        public void setMenuSource(Integer menuSource) {
            this.menuSource = menuSource;
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void buildListMenuView(Session session, Long idOfOrg) throws Exception {

        List<Item> items = new LinkedList<Item>();
        this.idOfOrg=idOfOrg;
        Criteria criteria = session.createCriteria(Menu.class);
        criteria.add(Restrictions.eq("org.idOfOrg",idOfOrg));
        List menies = criteria.list();
        for (Object object : menies) {
            Menu menu = (Menu) object;
            items.add(new Item(menu.getIdOfMenu(), menu.getMenuDate(), menu.getCreateTime(), menu.getMenuSource(), menu.getFlags()));
        }
        this.items=items;
    }

    public String getPageFilename() {
        return "org/menu/view";
    }

    private Long idOfOrder;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getIdOfOrder() {
        return idOfOrder;
    }

    public void setIdOfOrder(Long idOfOrder) {
        this.idOfOrder = idOfOrder;
    }
}
