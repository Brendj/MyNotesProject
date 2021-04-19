/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.webTechnolog;

import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.client.ClientGroupSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class ClientGroupListSelectPage extends BasicPage {

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

    private final Stack<ClientGroupSelectPage.CompleteHandler> completeHandlers = new Stack<ClientGroupSelectPage.CompleteHandler>();
    private List<ClientGroupSelectPage.Item> items = Collections.emptyList();
    private ClientGroupSelectPage.Item selectedItem = new ClientGroupSelectPage.Item();
    private String filter;
    private Long idOfOrg;

    public void pushCompleteHandler(ClientGroupSelectPage.CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeClientGroupSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeClientGroupSelection(session, selectedItem.getIdOfClientGroup());
            completeHandlers.pop();
        }
    }

    public List<ClientGroupSelectPage.Item> getItems() {
        return items;
    }

    public ClientGroupSelectPage.Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(ClientGroupSelectPage.Item selected) {
        if (null == selected) {
            this.selectedItem = new ClientGroupSelectPage.Item();
        } else {
            this.selectedItem = selected;
        }
    }

    public String getFilter() {
        return filter;
    }

    public Object cancelFilter() {
        selectedItem = new ClientGroupSelectPage.Item();
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
        List<ClientGroupSelectPage.Item> items = new ArrayList<ClientGroupSelectPage.Item>();
        if (filter == null) {
            filter = "";
        }
        List<ClientGroup> clientGroups = retrieveClientGroups(session, idOfOrg);
        for (ClientGroup clientGroup : clientGroups) {
            ClientGroupSelectPage.Item item = new ClientGroupSelectPage.Item(clientGroup);
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
