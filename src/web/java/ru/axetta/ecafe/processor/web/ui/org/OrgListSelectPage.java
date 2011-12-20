/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
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

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 02.11.11
 * Time: 18:39
 * To change this template use File | Settings | File Templates.
 */
public class OrgListSelectPage extends BasicPage {
    public interface CompleteHandlerList {
        void completeOrgListSelection(Map<Long, String> orgMap) throws Exception;
    }

    public static class Item {
        private Boolean selected;
        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public Item() {
            this.selected = null;
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        public Item(Org org) {
            this.selected = false;
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Boolean getSelected() {
            return selected;
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

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private List<Item> items = Collections.emptyList();
    private String filter;

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeOrgListSelection(boolean ok) throws Exception {
        Map<Long, String> orgMap = null;
        if (ok) {
            orgMap = new HashMap<Long, String>();
            for (Item item : items)
                if (item.getSelected()) {
                    orgMap.put(item.getIdOfOrg(), item.getShortName());
                }
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeOrgListSelection(orgMap);
            completeHandlerLists.pop();
        }
    }

    public List<Item> getItems() {
        return items;
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

    private List retrieveOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class);
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", filter, MatchMode.ANYWHERE),
                    Restrictions.like("officialName", filter, MatchMode.ANYWHERE)));
        }
        return criteria.list();
    }
}
