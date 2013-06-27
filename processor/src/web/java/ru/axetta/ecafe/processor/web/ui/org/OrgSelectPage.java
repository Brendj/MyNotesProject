/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

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

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<OrgShortItem> items = Collections.emptyList();
    private OrgShortItem selectedItem = new OrgShortItem();
    private String filter;
    private String tagFilter;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeOrgSelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
            completeHandlers.peek().completeOrgSelection(session, selectedItem.getIdOfOrg());
            completeHandlers.pop();
        }
    }

    public List<OrgShortItem> getItems() {
        return items;
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

    public String getTagFilter() {
        return tagFilter;
    }

    public void setTagFilter(String tagFilter) {
        this.tagFilter = tagFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void fill(Session session) throws Exception {
        this.items = retrieveOrgs(session);
    }

    public void fill(Session session, Long idOfOrg) throws Exception {
        List<OrgShortItem> items = retrieveOrgs(session);
        OrgShortItem selectedItem = new OrgShortItem();
        if (null != idOfOrg) {

            Criteria criteria = session.createCriteria(Org.class);
            criteria.add(Restrictions.eq("idOfOrg",idOfOrg));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.property("idOfOrg"),"idOfOrg")
                    .add(Projections.property("shortName"),"shortName")
                    .add(Projections.property("officialName"),"officialName")
            );

            criteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));
            selectedItem = (OrgShortItem) criteria.uniqueResult();
        }

        this.items = items;
        this.selectedItem = selectedItem;
    }

    @SuppressWarnings("unchecked")
    private List<OrgShortItem> retrieveOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class);
        //  Ограничение оргов, которые позволено видеть пользователю
        try {
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildOrgRestriction(idOfUser, criteria);
        } catch (Exception e) {
        }
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.or(Restrictions.like("shortName", filter, MatchMode.ANYWHERE),
                    Restrictions.like("officialName", filter, MatchMode.ANYWHERE)));
        }
        if (StringUtils.isNotEmpty(tagFilter)) {
            criteria.add(Restrictions.like("shortName", tagFilter, MatchMode.ANYWHERE));
        }
        criteria.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.property("idOfOrg")),"idOfOrg")
                .add(Projections.property("shortName"),"shortName")
                .add(Projections.property("officialName"),"officialName")
        );

        criteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));
        criteria.addOrder(Order.asc("idOfOrg"));
        return (List<OrgShortItem>) criteria.list();
    }

}