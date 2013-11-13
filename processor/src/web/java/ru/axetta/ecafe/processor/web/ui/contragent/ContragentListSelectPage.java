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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 * Time: 10:20:25
 * To change this template use File | Settings | File Templates.
 */
public class ContragentListSelectPage extends BasicPage {

    public interface CompleteHandler {

        void completeContragentListSelection(Session session, List<Long> idOfContragent, int multiContrFlag, String classTypes) throws Exception;
    }

    public static class Item {

        private final Long idOfContragent;
        private final String contragentName;
        private boolean selected;

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

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    //private Item selectedItem = new Item();
    private String filter, classTypesString;
    private int multiContrFlag;
    private String selectedIds;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeContragentSelection(Session session) throws Exception {
        List<Long> selected = new ArrayList<Long>();
        for (Item it : items) {
            if (!it.isSelected()) {
                continue;
            }
            selected.add(it.getIdOfContragent());
        }

        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeContragentListSelection(session, selected/*selectedItem.getIdOfContragent()*/,
                    multiContrFlag, classTypesString);
            completeHandlers.pop();
        }
    }

    public void cancelContragentListSelection() {
        completeHandlers.clear();
    }

    public List<Item> getItems() {
        return items;
    }

    public String getSelectedItems () {
        StringBuilder str = new StringBuilder();
        for (Item it : items) {
            if (!it.isSelected()) {
                continue;
            }
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(it.getContragentName());
        }
        return str.toString();
    }

    /*public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
    }*/

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(String selectedIds) {
        this.selectedIds = selectedIds;
    }

    public void fill(Session session, int multiContrFlag, String classTypes) throws Exception {
        this.multiContrFlag = multiContrFlag;
        List<Item> items = new ArrayList<Item>();
        List<String> selectedIdsList = Arrays.asList(StringUtils.split(selectedIds, ","));
        List contragents = retrieveContragents(session, classTypes);
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            Item item = new Item(contragent);
            items.add(item);
            if (selectedIdsList.contains(item.getIdOfContragent().toString())) {
                item.setSelected(true);
            }
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
        if(!classTypesString.isEmpty()) {
            String[] classTypes = classTypesString.split(",");
            Criterion exp = Restrictions.eq("classId", Integer.parseInt(classTypes[0]));
            for (int i = 1; i < classTypes.length; i++) {
                exp = Restrictions.or(exp, Restrictions.eq("classId", Integer.parseInt(classTypes[i])));
            }
            criteria.add(exp);
        }
        return criteria.list();
    }

}