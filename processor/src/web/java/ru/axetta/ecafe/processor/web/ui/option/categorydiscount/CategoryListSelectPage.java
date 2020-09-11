/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
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
 * Date: 18.01.12
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
public class CategoryListSelectPage extends BasicPage {
    public interface CompleteHandlerList {
        void completeCategoryListSelection(Map<Long, String> categoryMap) throws Exception;
    }

    public static class Item {

        private final Long idOfCategory;
        private final String categoryName;
        private Boolean selected;

        public Item() {
            this.idOfCategory = null;
            this.categoryName = null;
            this.selected = null;
        }

        public Item(CategoryDiscount category) {
            this.idOfCategory = category.getIdOfCategoryDiscount();
            this.categoryName = category.getCategoryName();
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

        public String getCategoryName() {
            return categoryName;
        }
    }

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private List<Item> items = Collections.emptyList();
    private String filter;

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }

    public void completeCategoryListSelection(boolean ok) throws Exception {
        Map<Long, String> categoryMap = null;
        if (ok) {
            categoryMap = new HashMap<Long, String>();
            for (Item item : items)
                if (item.getSelected()) {
                    categoryMap.put(item.getIdOfCategory(), item.getCategoryName());
                }
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeCategoryListSelection(categoryMap);
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

    public void fill(Session session, boolean flag, String categoryFilter) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List categoryDiscounts = retrieveCategory(session, flag);
        String[] idOfDiscounts = categoryFilter.split(",");
        Set<String> longSet = new HashSet<String>(Arrays.asList(idOfDiscounts));
        for (Object object : categoryDiscounts) {
            CategoryDiscount categoryDiscount = (CategoryDiscount) object;
            Item item = new Item(categoryDiscount);
            if(longSet.contains(String.valueOf(categoryDiscount.getIdOfCategoryDiscount()).trim())) item.setSelected(true);
            items.add(item);
        }
        this.items = items;
    }

    public void fill(Session session, boolean flag) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List categoryDiscounts = retrieveCategory(session, flag);
        for (Object object : categoryDiscounts) {
            CategoryDiscount categoryDiscount = (CategoryDiscount) object;
            Item item = new Item(categoryDiscount);
            items.add(item);
        }
        this.items = items;
    }

    private List retrieveCategory(Session session, boolean flag) throws HibernateException {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        criteria.addOrder(Order.asc("idOfCategoryDiscount"));
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.like("categoryName", filter, MatchMode.ANYWHERE).ignoreCase());
        }
        if(!flag) criteria.add(Restrictions.ge("idOfCategoryDiscount",Long.parseLong("0")));
        criteria.add(Restrictions.eq("deletedState", false));
        return criteria.list();
    }
}
