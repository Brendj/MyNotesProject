/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

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
public class CategorySelectPage extends BasicPage {

    public interface CompleteHandler {
        void completeCategorySelection(Session session, Long idOfCategory) throws Exception;
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

    private final Stack<CompleteHandler> completeHandlers = new Stack<CompleteHandler>();
    private List<Item> items = Collections.emptyList();
    private Item selectedItem = new Item();
    private List<Item> selItems = Collections.emptyList();
    private String filter;

    public void pushCompleteHandler(CompleteHandler handler) {
        completeHandlers.push(handler);
    }

    public void completeCategorySelection(Session session) throws Exception {
        if (!completeHandlers.empty()) {
           for (Item selectedItems: selItems) {
               if(selectedItems.getSelected()){
                   completeHandlers.peek().completeCategorySelection(session, selectedItems.getIdOfCategory());
                   completeHandlers.pop();
               }
           }
        }
    }

    public String getSelectedItems(){
         StringBuilder sb = new StringBuilder();
         for (Item item: this.getSelItems()){
             sb.append(item.getIdOfCategory());
             sb.append(",");
         }

         return sb.toString();
    }

    public List<Item> getSelItems() {
        return selItems;
    }

    public void setSelItems(List<Item> selItems) {
        this.selItems = selItems;
    }

    public List<Item> getItems() {
        return items;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selected) {
        if (null == selected) {
            this.selectedItem = new Item();
        } else {
            this.selectedItem = selected;
        }
        selItems.add(this.selectedItem);
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List categories = retrieveCategories(session);
        for (Object object : categories) {
            CategoryDiscount categoryDiscount = (CategoryDiscount) object;
            Item item = new Item(categoryDiscount);
            items.add(item);
        }
        this.items = items;
    }

    private List retrieveCategories(Session session) throws HibernateException {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        if (StringUtils.isNotEmpty(filter)) {
            criteria.add(Restrictions.like("categoryName", filter, MatchMode.ANYWHERE));
        }
        return criteria.list();
    }

}