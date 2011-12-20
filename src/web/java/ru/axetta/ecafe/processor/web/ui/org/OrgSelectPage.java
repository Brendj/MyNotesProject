/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
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
public class OrgSelectPage extends BasicPage {

    public interface CompleteHandler {
        void completeOrgSelection(Session session, Long idOfOrg) throws Exception;
    }

    public static class Item {
        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public Item() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        public Item(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private String filter;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeOrgSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeOrgSelection(session, selectedItem.getIdOfOrg());
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

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List orgs = retrieveOrgs(session);
        for (Object object : orgs) {
            Org org = (Org) object;
            items.add(new Item(org));
        }
        this.items = items;
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List orgs = retrieveOrgs(session);
        for (Object object : orgs) {
            Org org = (Org) object;
            items.add(new Item(org));
        }
        Item selectedItem = new Item();
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            selectedItem = new Item(org);
        }
        this.items = items;
        this.selectedItem = selectedItem;
    }

    private List retrieveOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class);
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", filter, MatchMode.ANYWHERE),
                    Restrictions.like("officialName", filter, MatchMode.ANYWHERE)));
        }
        return criteria.list();
    }

}