/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.List;

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
    private Integer supplierFilter = 0;

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeOrgListSelection(boolean ok) throws Exception {
        Map<Long, String> orgMap = null;
        if (ok) {
            updateSelectedOrgs();
            orgMap = new HashMap<Long, String>();
            orgMap.putAll(selectedOrgs);
            /*for (Item item : items) {
                if (item.getSelected()) {
                    orgMap.put(item.getIdOfOrg(), item.getShortName());
                }
            }*/
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

    public Integer getSupplierFilter() {
        return supplierFilter;
    }

    public void setSupplierFilter(Integer supplierFilter) {
        this.supplierFilter = supplierFilter;
    }

    public void fill(Session session, String orgFilter) throws Exception {
        updateSelectedOrgs();
        String[] idOfOrgs = orgFilter.split(",");
        Set<String> longSet = new HashSet<String>(Arrays.asList(idOfOrgs));
        ///
        for (String sId : longSet) {
            Long id = Long.parseLong(sId.trim());
            if (selectedOrgs.containsKey(id)) continue;
            Org org = (Org)session.get(Org.class, id);
            selectedOrgs.put(id, org.getShortName());
        }
        ///
        List<Item> items = new LinkedList<Item>();
        List orgs = retrieveOrgs(session);
        for (Object object : orgs) {
            Org org = (Org) object;
            Item item = new Item(org);
            if (selectedOrgs.containsKey(org.getIdOfOrg())) item.setSelected(true);
            items.add(item);
        }
        this.items = items;
    }

    HashMap<Long, String> selectedOrgs;
    
    public void onShow() {
        selectedOrgs = new HashMap<Long, String>();
        this.items = new LinkedList<Item>();
    }
    
    public void fill(Session session) throws Exception {
        updateSelectedOrgs();
        ////
        List<Item> items = new LinkedList<Item>();
        List orgs = retrieveOrgs(session);
        for (Object object : orgs) {
            Org org = (Org) object;
            Item i = new Item(org);
            if (selectedOrgs.containsKey(i.getIdOfOrg())) i.setSelected(Boolean.TRUE);
            items.add(i);
        }
        this.items = items;
    }

    private void updateSelectedOrgs() {
        for (Item i : this.items) {
            if (i.getSelected()) {
                selectedOrgs.put(i.getIdOfOrg(), i.getShortName());
            } else {
                selectedOrgs.remove(i.getIdOfOrg());
            }
        }
    }

    private List retrieveOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.addOrder(Order.asc("idOfOrg"));
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", filter, MatchMode.ANYWHERE),
                    Restrictions.like("officialName", filter, MatchMode.ANYWHERE)));
        }
        if (supplierFilter != null && supplierFilter != 0) {
            Criteria destMenuExchangeCriteria = session.createCriteria(MenuExchangeRule.class);
            List menuExchangeRuleList = destMenuExchangeCriteria.list();
            HashSet<Long> idOfSourceOrgSet = new HashSet<Long>();
            for (Object object : menuExchangeRuleList) {
                MenuExchangeRule menuExchangeRule = (MenuExchangeRule) object;
                Long idOfSourceOrg = menuExchangeRule.getIdOfSourceOrg();
                if (idOfSourceOrg != null) {
                    idOfSourceOrgSet.add(idOfSourceOrg);
                }
            }
            Criterion criterion = Restrictions.in("idOfOrg", idOfSourceOrgSet);
            if (supplierFilter == 1) {
                criterion = Restrictions.not(criterion);
            }
            criteria.add(criterion);
        }

        deselectAllItems();
        return criteria.list();
    }

    public void deselectAllItems() {
        for (Item item : items) {
            item.setSelected(false);
        }
    }

    public void selectAllItems() {
        for (Item item : items) {
            item.setSelected(true);
        }
    }

}
