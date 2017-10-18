/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.*;

public class OrgMainBuildingListSelectPage extends BasicPage {

    public interface CompleteHandler {
        void completeOrgMainBuildingListSelection(Session session, List<Org> orgs) throws Exception;
    }

    public static class Item {

        private final Long idOfOrg;
        private final String shortNameInfoService;
        private boolean selected;

        public Item() {
            this.idOfOrg = null;
            this.shortNameInfoService = null;
        }

        public Item(Org organization) {
            this.idOfOrg = organization.getIdOfOrg();
            this.shortNameInfoService = organization.getShortNameInfoService();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortNameInfoService() {
            return shortNameInfoService;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private final Stack<OrgMainBuildingListSelectPage.CompleteHandler> completeHandlers = new Stack<OrgMainBuildingListSelectPage.CompleteHandler>();
    private List<OrgMainBuildingListSelectPage.Item> items = Collections.emptyList();
    private String filter;
    private Long selectedMainOrgId;
    //private Item selectedItem;

    public void pushCompleteHandler(OrgMainBuildingListSelectPage.CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeOrgMainBuildingSelection(Session session, Long organizationId) throws Exception {

        List<Org> orgList = new ArrayList<Org>();
        Criteria criteria;

        if (null != organizationId) {
            criteria = session.createCriteria(Org.class).addOrder(Order.asc("idOfOrg"));
            criteria.add(Restrictions.eq("idOfOrg", organizationId));
            Org org = (Org) criteria.uniqueResult();
            if (null != org) {
                orgList.addAll(org.getFriendlyOrg());
            }
        }

        if (!completeHandlers.empty()) {
            completeHandlers.peek()
                    .completeOrgMainBuildingListSelection(session, orgList);
            completeHandlers.pop();
        }
    }

    public void cancelOrgMainBuildingListSelection() {
        completeHandlers.clear();
    }

    public List<OrgMainBuildingListSelectPage.Item> getItems() {
        return items;
    }

    public void setItems(List<OrgMainBuildingListSelectPage.Item> items) {
        this.items = items;
    }

    public String getSelectedItems() {
        StringBuilder str = new StringBuilder();
        for (OrgMainBuildingListSelectPage.Item it : items) {
            if (!it.isSelected()) {
                continue;
            }
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(it.getShortNameInfoService());
        }
        return str.toString();
    }

    public Object updateSelectedIds(Long idOfOrg, Boolean isSelected) {

        if (null != idOfOrg && null != isSelected) {
            if (isSelected) {
                selectedMainOrgId = idOfOrg;
                MainPage.getSessionInstance().getUserEditPage().setOrganizationId(idOfOrg);
            } else {
                selectedMainOrgId = -1L;
            }
        }

        for (Item i : this.getItems()) {
            if (i.getIdOfOrg().equals(selectedMainOrgId)) {
                i.setSelected(!i.isSelected());
            } else {
                i.setSelected(false);
            }
        }

        return null;
    }

    public Object cancelFilter() {
        items = Collections.emptyList();
        return null;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void fill(Session session, Long organizationId) throws Exception {
        selectedMainOrgId = organizationId;
        List<OrgMainBuildingListSelectPage.Item> items = new ArrayList<OrgMainBuildingListSelectPage.Item>();
        List orgs = retrieveOrgs(session);
        //updateSelectedIds();
        for (Object object : orgs) {
            Org org = (Org) object;
            OrgMainBuildingListSelectPage.Item item = new OrgMainBuildingListSelectPage.Item(org);
            items.add(item);
            if (null != selectedMainOrgId) {
                if (selectedMainOrgId.equals(item.getIdOfOrg())) {
                    item.setSelected(true);
                }
            }
        }
        this.items = items;
    }

    private List retrieveOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class).addOrder(Order.asc("shortNameInfoService"));

        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.or(Restrictions.ilike("shortName", filter, MatchMode.ANYWHERE),
                            Restrictions.ilike("officialName", filter, MatchMode.ANYWHERE)));
        }

        criteria.add(Restrictions.eq("mainBuilding", Boolean.TRUE));                            // only mainbuildings
        criteria.add(Restrictions.ne("type", OrganizationType.SUPPLIER));
        //List<Org> orgsByCriteria = criteria.list();

        return criteria.list();
    }

    public void deselectAllItems() {
        for (OrgMainBuildingListSelectPage.Item item : getItems()) {
            item.setSelected(false);
        }
    }

    public Long getSelectedMainOrgId() {
        return selectedMainOrgId;
    }

    public void setSelectedMainOrgId(Long selectedMainOrgId) {
        this.selectedMainOrgId = selectedMainOrgId;
    }

    //public Item getSelectedItem() {
    //    return selectedItem;
    //}
    //
    //public void setSelectedItem(Item selectedItem) {
    //    this.selectedItem = selectedItem;
    //}
}
