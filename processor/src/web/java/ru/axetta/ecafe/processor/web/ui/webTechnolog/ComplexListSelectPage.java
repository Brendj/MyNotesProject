/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.webTechnolog;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.math.BigInteger;
import java.util.*;

public class ComplexListSelectPage extends BasicPage {
    private String filter;

    public String getFilterContagents() {
        return filterContagents;
    }

    public void setFilterContagents(String filterContagents) {
        this.filterContagents = filterContagents;
    }

    public List<Long> getFilterOrgs() {
        return filterOrgs;
    }

    public void setFilterOrgs(List<Long> filterOrgs) {
        this.filterOrgs = filterOrgs;
    }

    public interface CompleteHandler {
        void complexListSelection(Session session, List<Long> idOfComplex) throws Exception;
    }

    public static class Item {

        private final Long idOfComplex;
        private final String complexName;
        private boolean selected;

        public Item() {
            this.idOfComplex = null;
            this.complexName = null;
        }

        public Item(WtComplex wtComplex) {
            this.idOfComplex = wtComplex.getIdOfComplex();
            this.complexName = wtComplex.getName();
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public String getComplexName() {
            return complexName;
        }

        public Long getIdOfComplex() {
            return idOfComplex;
        }
    }

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private String selectedIds;
    private String filterContagents;
    private List<Long> filterOrgs;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeComplexSelection(Session session) throws Exception {
        List<String> list = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<Long> selected = new ArrayList<Long>();
        for(String s : list){
            selected.add(Long.parseLong(s));
        }

        if (!completeHandlers.empty()) {
            completeHandlers.peek()
                    .complexListSelection(session, selected);
            completeHandlers.pop();
        }
    }

    public void cancelContragentListSelection() {
        completeHandlers.clear();
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getSelectedItems() {
        StringBuilder str = new StringBuilder();
        for (Item it : items) {
            if (!it.isSelected()) {
                continue;
            }
            if (str.length() > 0) {
                str.append("; ");
            }
            str.append(it.getComplexName());
        }
        return str.toString();
    }

    public void updateSelectedIds(Long id, boolean selected) {
        List<String> list = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<String> selectedIdsList = new ArrayList<String>(list);
        if (selectedIdsList.contains(id.toString()) && !selected) {
            selectedIdsList.remove(id.toString());
        } else {
            if (!selectedIdsList.contains(id.toString()) && selected) {
                selectedIdsList.add(id.toString());
            }
        }
        StringBuilder str = new StringBuilder();
        for (String s : selectedIdsList) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(s);
        }
        selectedIds = str.toString();
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

    public String getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(String selectedIds) {
        this.selectedIds = selectedIds;
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new ArrayList<Item>();
        List<String> selectedIdsList = Arrays.asList(StringUtils.split(selectedIds, ","));
        List<String> filtercontagent = Arrays.asList(StringUtils.split(filterContagents, ","));
        List<Contragent> contragents = new ArrayList<>();
        if (!filtercontagent.isEmpty()) {
            for (String idContagents : filtercontagent) {
                Contragent contragent = (Contragent) session.load(Contragent.class, Long.valueOf(idContagents));
                contragents.add(contragent);
            }
        }

        List complexes = retrieveComplexes(session, contragents, filterOrgs);
        for (Object object : complexes) {
            WtComplex wtComplex = (WtComplex) object;
            Item item = new Item(wtComplex);
            items.add(item);
            if (selectedIdsList.contains(item.getIdOfComplex().toString())) {
                item.setSelected(true);
            }
        }
        this.items = items;
    }

    private List retrieveComplexes(Session session, List<Contragent> contragents, List<Long> orgs) throws HibernateException {
        Criteria criteria = session.createCriteria(WtComplex.class).addOrder(Order.asc("name"));
        Criteria criteria1 = session.createCriteria(WtComplex.class).addOrder(Order.asc("name"));
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.ilike("name", filter, MatchMode.ANYWHERE));
            criteria1.add(Restrictions.ilike("name", filter, MatchMode.ANYWHERE));
        }
        if (contragents != null && !contragents.isEmpty()){
            criteria.add(Restrictions.in("contragent", contragents));
            criteria1.add(Restrictions.in("contragent", contragents));
        }
        if (orgs != null && !orgs.isEmpty()) {
            String getGroup = "select r.idoforggroup "
                    + "from cf_wt_org_group_relations r "
                    + "where r.idoforg in (:orgId) ";
            Query queryOrg = session.createSQLQuery(getGroup);
            queryOrg.setParameterList("orgId", orgs);
            List<BigInteger> orgData = queryOrg.list();
            List<Long> groups = new ArrayList<>();
            for (BigInteger orgDatum : orgData) {
                groups.add(orgDatum.longValue());
            }
            criteria1.add(Restrictions.in("idOfOrgGroup", groups));
            criteria.createAlias("orgs", "orgsIntrernal");
            criteria.add(Restrictions.in("orgsIntrernal.idOfOrg", orgs));
        }
        criteria.add(Restrictions.eq("deleteState", 0));
        criteria1.add(Restrictions.eq("deleteState", 0));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        criteria1.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<WtComplex> complexByCriteria = criteria.list();
        complexByCriteria.addAll(criteria1.list());
        return complexByCriteria;
    }

    public void selectAllItems() {
        for (Item item : getItems()) {
            item.setSelected(true);
        }
        selectedIds = "";
        StringBuilder str = new StringBuilder();
        for (Item item : items) {
            if (str.length() > 0) {
                str.append(",");
            }
            str.append(item.getIdOfComplex());
        }
        selectedIds = str.toString();
    }

    public void deselectAllItems() {
        selectedIds = "";
        for (Item item : getItems()) {
            item.setSelected(false);
        }
    }

}