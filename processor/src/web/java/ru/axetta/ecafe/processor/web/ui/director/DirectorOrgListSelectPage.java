/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.director;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.DirectorPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectionBasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.transform.Transformers;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 02.11.11
 */
@Component
@Scope("session")
public class DirectorOrgListSelectPage extends OrgSelectionBasicPage {

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private Map<Long, String> selectedOrgs = new HashMap<Long, String>();
    private String selectedOrgsString = "";
    private List<OrgShortItem> autoCompleteOrgs = new ArrayList<OrgShortItem>();

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeOrgListSelection(boolean ok) throws Exception {
        setFilterMode(0);
        resetAvailableOrganizationTypes();

        Map<Long, String> orgMap = null;
        if (ok) {
            updateSelectedOrgs();
            orgMap = new HashMap<Long, String>(selectedOrgs);
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeDirectorOrgListSelection(orgMap);
            completeHandlerLists.pop();
        }
    }

    public synchronized void fill(Session session, String orgFilter, Boolean isUpdate, List<Long> idOfContragentOrgList,
        List<Long> idOfContragentList, DirectorPage mainPage) throws Exception {
        if (isUpdate) {
            updateSelectedOrgs();
            mainPage.setOrgFilterOfSelectOrgListSelectPage(StringUtils.join(selectedOrgs.values(), ","));
        } else {
            selectedOrgs.clear();
        }
        String[] idOfOrgs = orgFilter.split(",");
        Set<String> longSet = new HashSet<String>(Arrays.asList(idOfOrgs));
        ///
        for (String sId : longSet) {
            try {
                Long id = Long.parseLong(sId.trim());
                if (selectedOrgs.containsKey(id)) {
                    continue;
                }
                Org org = (Org) session.get(Org.class, id);
                selectedOrgs.put(id, org.getShortName());
            } catch (Exception ignored) {
                System.out.println("exception");
            }
        }
        ///
        List<OrgShortItem> items = retrieveOrgs(session, idOfContragentOrgList, idOfContragentList);
        for (OrgShortItem orgShortItem : items) {
            orgShortItem.setSelected(selectedOrgs.containsKey(orgShortItem.getIdOfOrg()));
        }
        this.items = items;
        this.autoCompleteOrgs = fillAutoCompleteOrgs(session);
    }

    public synchronized void fill(Session session, Boolean isUpdate, List<Long> idOfContragentOrgList, List<Long> idOfContragentList)
            throws Exception {
        if (isUpdate) {
            updateSelectedOrgs();
        } else {
            selectedOrgs.clear();
        }
        List<OrgShortItem> items = retrieveOrgs(session, idOfContragentOrgList, idOfContragentList);
        for (OrgShortItem orgShortItem : items) {
            orgShortItem.setSelected(selectedOrgs.containsKey(orgShortItem.getIdOfOrg()));
        }
        this.autoCompleteOrgs = fillAutoCompleteOrgs(session);
        this.items = items;
    }

    public List<OrgShortItem> fillAutoCompleteOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.addOrder(Order.asc("idOfOrg"));
        criteria.setProjection(
                Projections.projectionList().add(Projections.distinct(Projections.property("idOfOrg")), "idOfOrg")
                        .add(Projections.property("shortName"), "shortName")
                        .add(Projections.property("officialName"), "officialName")
                        .add(Projections.property("address"), "address"));
        criteria.setCacheMode(CacheMode.NORMAL);
        criteria.setCacheable(true);
        criteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));
        return (List<OrgShortItem>) criteria.list();
    }

    private void updateSelectedOrgs() {
        for (OrgShortItem i : this.getItems()) {
            if (i.getSelected()) {
                selectedOrgs.put(i.getIdOfOrg(), i.getShortName());
            } else {
                selectedOrgs.remove(i.getIdOfOrg());
            }
        }
    }

    public List<OrgShortItem> autoComplete(Object suggest) {
        String pref = (String) suggest;
        List<OrgShortItem> result = new ArrayList<OrgShortItem>();
        for (OrgShortItem elem : getAutoCompleteOrgs()) {
            if ((elem.getShortName() != null && elem.getShortName().toLowerCase().contains(pref.toLowerCase())) || ""
                    .equals(pref)) {
                result.add(elem);
            }
        }
        return result;
    }

    public List<OrgShortItem> getAutoCompleteOrgs() {
        return autoCompleteOrgs;
    }

    public interface CompleteHandlerList {

        void completeDirectorOrgListSelection(Map<Long, String> orgMap) throws Exception;
    }

    public Map<Long, String> getSelectedOrgs() {
        return selectedOrgs;
    }

    public void setSelectedOrgs(Map<Long, String> selectedOrgs) {
        this.selectedOrgs = selectedOrgs;
    }

    public String getSelectedOrgsString() {
        String s = "";
        for (String org : getSelectedOrgs().values()) {
            s += org + ", ";
        }
        if (s.length() > 2) {
            return s.substring(0, s.length() - 2);
        }
        return s;
    }
}
