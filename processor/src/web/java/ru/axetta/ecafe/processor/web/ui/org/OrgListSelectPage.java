/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.transform.Transformers;

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

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private List<OrgShortItem> items = Collections.emptyList();
    private String filter;
    private String tagFilter;
    /*
       0               - нет фильтра
       1               - фильтр "только ОУ"
       другое значение - фильтр "только поставщики"
    */
    private int  supplierFilter = 0;
    /*
       0 - доступны все фильтры
       1 - доступен только фильтр по ОУ
       2 - доступен только фильтр по поставщикам
       3 - доступны только фильтры по ОУ и по поставщикам
    */
    private int filterMode = 0;
    private boolean allOrgFilterDisabled = false;
    private boolean schoolFilterDisabled = false;
    private boolean supplierFilterDisabled = false;

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeOrgListSelection(boolean ok) throws Exception {
        setFilterMode(0);

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

    public List<OrgShortItem> getItems() {
        return items;
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

    public int getSupplierFilter() {
        return supplierFilter;
    }

    public void setSupplierFilter(int supplierFilter) {
        this.supplierFilter = supplierFilter;
    }

    public int getFilterMode() {
        return filterMode;
    }

    public void setFilterMode(int filterMode) {
        this.filterMode = filterMode;
        switch (filterMode) {
            case 1:
                setOrgFilterModeParameters(true, false, true);
                supplierFilter = 1;
                break;
            case 2:
                setOrgFilterModeParameters(true, true, false);
                supplierFilter = 2;
                break;
            case 3:
                setOrgFilterModeParameters(true, false, false);
                supplierFilter = 1;
                break;
            default:
                setOrgFilterModeParameters(false, false, false);
                supplierFilter = 0;
                break;
        }
    }

    private void setOrgFilterModeParameters(boolean allOrgFilterDisabled, boolean schoolFilterDisabled, boolean supplierFilterDisabled) {
        this.allOrgFilterDisabled = allOrgFilterDisabled;
        this.schoolFilterDisabled = schoolFilterDisabled;
        this.supplierFilterDisabled = supplierFilterDisabled;
    }

    public boolean getAllOrgFilterDisabled() {
        return allOrgFilterDisabled;
    }

    public boolean getSchoolFilterDisabled() {
        return schoolFilterDisabled;
    }


    public boolean getSupplierFilterDisabled() {
        return supplierFilterDisabled;
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
        List<OrgShortItem> items = retrieveOrgs(session);
        for (OrgShortItem orgShortItem: items){
            orgShortItem.setSelected(selectedOrgs.containsKey(orgShortItem.getIdOfOrg()));
        }

        this.items = items;
    }

    HashMap<Long, String> selectedOrgs;
    
    public void onShow() {
        selectedOrgs = new HashMap<Long, String>();
        this.items = new LinkedList<OrgShortItem>();
    }
    
    public void fill(Session session) throws Exception {
        updateSelectedOrgs();
        List<OrgShortItem> items = retrieveOrgs(session);
        for (OrgShortItem orgShortItem: items){
            orgShortItem.setSelected(selectedOrgs.containsKey(orgShortItem.getIdOfOrg()));
        }
        this.items = items;
    }

    private void updateSelectedOrgs() {
        for (OrgShortItem i : this.items) {
            if (i.getSelected()) {
                selectedOrgs.put(i.getIdOfOrg(), i.getShortName());
            } else {
                selectedOrgs.remove(i.getIdOfOrg());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<OrgShortItem> retrieveOrgs(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(Org.class);
        criteria.addOrder(Order.asc("idOfOrg"));
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
            criteria.add(Restrictions.like("tag", tagFilter, MatchMode.ANYWHERE));
        }
        if (supplierFilter != 0) {
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

        criteria.setProjection(Projections.projectionList()
                .add(Projections.distinct(Projections.property("idOfOrg")),"idOfOrg")
                .add(Projections.property("shortName"),"shortName")
                .add(Projections.property("officialName"),"officialName")
        );

        criteria.setResultTransformer(Transformers.aliasToBean(OrgShortItem.class));

        deselectAllItems();
        return (List<OrgShortItem>) criteria.list();
    }

    public void deselectAllItems() {
        for (OrgShortItem item : items) {
            item.setSelected(false);
        }
    }

    public void selectAllItems() {
        for (OrgShortItem item : items) {
            item.setSelected(true);
        }
    }

}
