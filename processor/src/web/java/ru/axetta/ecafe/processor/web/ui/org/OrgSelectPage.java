/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 03.07.2009
 */
public class OrgSelectPage extends OrgSelectionBasicPage {

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private OrgShortItem selectedItem = new OrgShortItem();

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeOrgSelection(Session session) throws Exception {
        setFilterMode(0);
        resetAvailableOrganizationTypes();

        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeOrgSelection(session, selectedItem.getIdOfOrg());
            completeHandlers.pop();
        }
    }

    public void cancelOrgSelection() {
        setFilterMode(0);
        resetAvailableOrganizationTypes();
        completeHandlers.clear();
    }

    public Object cancelFilter() {
        selectedItem = new OrgShortItem();
        MainPage.getSessionInstance().updateOrgSelectPage();
        return null;
    }

    public void fill(Session session, List<Long> idOfMenuSourceOrgList) throws Exception {
        this.idOfSelectedContragent = -1L;
        this.idOfContract = null;
        this.items = retrieveOrgs(session, idOfMenuSourceOrgList);
    }

    public void fill(Long idOfContragent, Long idOfContract, Session session, List<Long> idOfMenuSourceOrgList)
            throws Exception {
        this.idOfSelectedContragent = idOfContragent;
        this.idOfContract = idOfContract;
        this.filter = "";
        this.idFilter = "";
        this.items = retrieveOrgs(session, idOfMenuSourceOrgList);
        idFilter = "";
    }

    public void fill(Session session, Long idOfOrg, List<Long> idOfMenuSourceOrgList) throws Exception {
        List<OrgShortItem> items = retrieveOrgs(session, idOfMenuSourceOrgList);
        OrgShortItem selectedItem = new OrgShortItem();
        if (null != idOfOrg) {

            Criteria criteria = session.createCriteria(Org.class);
            criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            criteria.setProjection(Projections.projectionList().add(Projections.property("idOfOrg"), "idOfOrg")
                    .add(Projections.property("shortName"), "shortName")
                    .add(Projections.property("officialName"), "officialName"));
            criteria.setCacheMode(CacheMode.NORMAL);
            criteria.setCacheable(true);
            criteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));
            selectedItem = (OrgShortItem) criteria.uniqueResult();
        }

        this.items = items;
        this.selectedItem = selectedItem;
    }

    public OrgShortItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(OrgShortItem selected) {
        if (null == selected) {
            this.selectedItem = new OrgShortItem();
        } else {
            this.selectedItem = selected;
        }
    }

    public interface CompleteHandler {
        void completeOrgSelection(Session session, Long idOfOrg) throws Exception;
    }

    public void clearFilter(){
        setFilter("");
        setIdFilter("");
        setOrgIdFromNsi(null);
        setEkisId(null);
        setRegion("");
        setIdOfSelectedContragent(-1L);
    }

}