/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
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
 * Date: 20.01.12
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class RuleListSelectPage extends BasicPage {
    public interface CompleteHandlerList {
        void completeRuleListSelection(Map<Long, String> ruleMap) throws Exception;
    }

    public static class Item{
        private Long idOfRule;
        private String description;
        private Boolean selected;

        public Item(){
            this.description=null;
            this.idOfRule=null;
            this.selected=null;
        }

        public Item(DiscountRule discountRule){
            this.description=discountRule.getDescription();
            this.idOfRule=discountRule.getIdOfRule();
            this.selected=false;
        }

        public Long getIdOfRule() {
            return idOfRule;
        }

        public void setIdOfRule(Long idOfRule) {
            this.idOfRule = idOfRule;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }
    }

    private final Stack<CompleteHandlerList> completeHandlerLists = new Stack<CompleteHandlerList>();
    private List<Item> items = Collections.emptyList();
    private String filter;

    public void pushCompleteHandlerList(CompleteHandlerList handlerList) {
        completeHandlerLists.push(handlerList);
    }
    public void completeRuleListSelection(boolean ok) throws Exception {
        Map<Long, String> ruleMap = null;
        if (ok) {
            ruleMap = new HashMap<Long, String>();
            for (Item item : items)
                if (item.getSelected()) {
                    ruleMap.put(item.getIdOfRule(), item.getDescription());
                }
        }
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().completeRuleListSelection(ruleMap);
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

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        List rules = retrieveRule(session);
        for(Object object: rules){
            DiscountRule discountRule = (DiscountRule) object;
            items.add(new Item(discountRule));
        }
        this.items=items;
    }
    
    private List retrieveRule(Session session) throws HibernateException{
        Criteria ruleCriteria = session.createCriteria(DiscountRule.class);
        ruleCriteria.addOrder(Order.asc("idOfRule"));
        if(StringUtils.isNotEmpty(filter)){
            ruleCriteria.add(
              Restrictions.like("description", filter, MatchMode.ANYWHERE)
            );
        }
        return ruleCriteria.list();
    }
}
