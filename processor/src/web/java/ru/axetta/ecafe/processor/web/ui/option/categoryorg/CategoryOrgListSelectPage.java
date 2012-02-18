/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categoryorg;

import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 14.02.12
 * Time: 11:39
 * To change this template use File | Settings | File Templates.
 */
public class CategoryOrgListSelectPage extends BasicPage {
    public interface CompleteHandlerList {
        void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception;
    }

    public static class Item {

        private final Long idOfCategory;
        private final String categoryOrgName;
        private Boolean selected;

        public Item() {
            this.idOfCategory = null;
            this.categoryOrgName = null;
            this.selected = null;
        }

        public Item(CategoryOrg category) {
            this.idOfCategory = category.getIdOfCategoryOrg();
            this.categoryOrgName = category.getCategoryName();
            this.selected = false;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }

        public Long getIdOfCategory() {
            return idOfCategory;
        }

        public String getCategoryOrgName() {
            return categoryOrgName;
        }
    }

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private List<Item> items = Collections.emptyList();
    private String filter;

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeCategoryOrgListSelection(boolean ok) throws Exception {
        Map<Long, String> categoryOrgMap = null;
        if (ok) {
            categoryOrgMap = new HashMap<Long, String>();
            for (Item item : items)
                if (item.getSelected()) {
                    categoryOrgMap.put(item.getIdOfCategory(), item.getCategoryOrgName());
                }
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeCategoryOrgListSelection(categoryOrgMap);
            completeHandlerLists.pop();
        }
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List categoryOrgs = retrieveCategory(session);
        for (Object object : categoryOrgs) {
            CategoryOrg categoryOrg = (CategoryOrg) object;
            Item item = new Item(categoryOrg);

            items.add(item);
        }
        this.items = items;
    }

    private List retrieveCategory(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(CategoryOrg.class);
        criteria.addOrder(Order.asc("idOfCategoryOrg"));
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.like("categoryName", filter, MatchMode.ANYWHERE));
        }
        return criteria.list();
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
}
