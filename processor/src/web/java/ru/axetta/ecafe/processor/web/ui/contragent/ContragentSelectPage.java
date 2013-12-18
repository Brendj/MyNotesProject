/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 10:20:25
 * To change this template use File | Settings | File Templates.
 */
public class ContragentSelectPage extends BasicPage {

    public interface CompleteHandler {

        void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes) throws Exception;
    }

    public static class Item {

        private final Long idOfContragent;
        private final String contragentName;

        public Item() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Item(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private String filter, classTypesString;
    private int multiContrFlag;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeContragentSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeContragentSelection(session, selectedItem.getIdOfContragent(),
                    multiContrFlag, classTypesString);
            completeHandlers.pop();
        }
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void fill(Session session, int multiContrFlag, String classTypes) throws Exception {
        this.multiContrFlag = multiContrFlag;
        List<Item> items = new LinkedList<Item>();
        List contragents = retrieveContragents(session, classTypes);
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            Item item = new Item(contragent);
            items.add(item);
        }
        this.items = items;
    }

    /*public void fill(Session session, Long idOfContragent) throws HibernateException {
        List<Item> items = new LinkedList<Item>();
        List contragents = retrieveContragents(session);
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            Item item = new Item(contragent);
            items.add(item);
        }
        Item selectedItem = new Item();
        if (null != idOfContragent) {
            Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
            selectedItem = new Item(contragent);
        }
        this.items = items;
        this.selectedItem = selectedItem;
    }*/

    private List retrieveContragents(Session session, String classTypesString) throws HibernateException {
        this.classTypesString = classTypesString;
        Criteria criteria = session.createCriteria(Contragent.class).addOrder(Order.asc("contragentName"));
        //  Ограничение на просмотр только тех контрагентов, которые доступны пользователю
        try {
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildContragentRestriction(idOfUser, criteria);
        } catch (Exception e) {
        }
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.like("contragentName", filter, MatchMode.ANYWHERE));
        }
        if(StringUtils.isNotEmpty(classTypesString)) {
            String[] classTypes = classTypesString.split(",");
            Criterion exp = Restrictions.eq("classId", Integer.parseInt(classTypes[0]));
            for (int i = 1; i < classTypes.length; i++) {
                exp = Restrictions.or(exp, Restrictions.eq("classId", Integer.parseInt(classTypes[i])));
            }
            criteria.add(exp);
        }
        criteria.addOrder(Order.asc("contragentName"));
        return criteria.list();
    }

}