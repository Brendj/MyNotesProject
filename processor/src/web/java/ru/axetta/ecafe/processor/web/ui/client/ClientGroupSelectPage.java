/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 29.02.12
 * Time: 13:25
 * To change this template use File | Settings | File Templates.
 */
public class ClientGroupSelectPage extends BasicPage {

    public interface CompleteHandler {

        void completeClientGroupSelection(Session session, Long idOfClientGroup) throws Exception;
    }

    public static class Item {

        private Long idOfClientGroup;
        private final String groupName;
        private Boolean selected;

        public Item() {
            this.idOfClientGroup = null;
            this.groupName = null;
            this.selected = null;
        }

        public Item(ClientGroup clientGroup) {
            this.idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
            this.groupName = clientGroup.getGroupName();
            this.selected = false;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public String getGroupName() {
            return groupName;
        }

        public Long getIdOfClientGroup() {
            return idOfClientGroup;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private String filter;
    private Long idOfOrg;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeClientGroupSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeClientGroupSelection(session, selectedItem.getIdOfClientGroup());
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

    public Object cancelFilter() {
        selectedItem = new Item();
        return null;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        List<Item> items = new ArrayList<Item>();
        if (filter == null) {
            filter = "";
        }
        List<ClientGroup> clientGroups = retrieveClientGroups(session, idOfOrg);
        for (ClientGroup clientGroup : clientGroups) {
            Item item = new Item(clientGroup);
            if (!(item.getGroupName().isEmpty() || item.getGroupName() == null || filter.isEmpty())) {
                if (item.getGroupName().toLowerCase().contains(filter.toLowerCase())) {
                    items.add(item);
                }
            } else {
                items.add(item);
            }
        }

        this.items = items;
    }

    private List<ClientGroup> retrieveClientGroups(Session session, Long idOfOrg) throws HibernateException {
        Criteria criteria = session.createCriteria(ClientGroup.class);
        criteria.add(Restrictions.eq("org.idOfOrg", idOfOrg));
        return criteria.list();
    }
}
