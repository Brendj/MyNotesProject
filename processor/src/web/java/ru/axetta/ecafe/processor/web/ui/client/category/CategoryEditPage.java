/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.client.rule.RuleListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public class CategoryEditPage extends BasicWorkspacePage implements RuleListSelectPage.CompleteHandlerList {

    public String getPageFilename() {
        return "client/category/edit";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String discountRules;
    private String description;
    private Date createdDate;
    private Date lastUpdate;
    private String filter = "Не выбрано";
    private List<Long> idOfRuleList = new ArrayList<Long>();
    private Set<DiscountRule> discountRuleSet;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Long> getIdOfRuleList() {
        return idOfRuleList;
    }

    public void setIdOfRuleList(List<Long> idOfRuleList) {
        this.idOfRuleList = idOfRuleList;
    }

    public Set<DiscountRule> getDiscountRuleSet() {
        return discountRuleSet;
    }

    public void setDiscountRuleSet(Set<DiscountRule> discountRuleSet) {
        this.discountRuleSet = discountRuleSet;
    }

    public long getIdOfCategoryDiscount() {
        return idOfCategoryDiscount;
    }

    public void setIdOfCategoryDiscount(long idOfCategoryDiscount) {
        this.idOfCategoryDiscount = idOfCategoryDiscount;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDiscountRules() {
        return discountRules;
    }

    public void setDiscountRules(String discountRules) {
        this.discountRules = discountRules;
    }

    public void fill(Session session, Long idOfCategoryDiscount) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount) session.load(CategoryDiscount.class, idOfCategoryDiscount);

        fill(categoryDiscount);
    }

    public void updateCategory(Session persistenceSession, Long idOfCategory) throws Exception {
        CategoryDiscount categoryDiscount = (CategoryDiscount) persistenceSession.load(CategoryDiscount.class, idOfCategory);
        categoryDiscount.setIdOfCategoryDiscount(idOfCategoryDiscount);
        categoryDiscount.setCategoryName(categoryName);
        categoryDiscount.setDiscountRules(discountRules);
        categoryDiscount.setDescription(description);
        categoryDiscount.setCreatedDate(createdDate);
        categoryDiscount.setLastUpdate(lastUpdate);
        if(!this.idOfRuleList.isEmpty()){
            this.discountRuleSet = new HashSet<DiscountRule>();
            Criteria categoryCrioteria = persistenceSession.createCriteria(DiscountRule.class);
            categoryCrioteria.add(Restrictions.in("idOfRule", this.idOfRuleList));
            for (Object object: categoryCrioteria.list()){
                this.discountRuleSet.add((DiscountRule) object);
            }
            categoryDiscount.setDiscountsRules(this.discountRuleSet);
        } else {
            categoryDiscount.getDiscountsRules().clear();
        }
        persistenceSession.update(categoryDiscount);
        fill(categoryDiscount);
    }

    private void fill(CategoryDiscount categoryDiscount) throws Exception {
        this.idOfCategoryDiscount = categoryDiscount.getIdOfCategoryDiscount();
        this.categoryName = categoryDiscount.getCategoryName();
        this.discountRules = categoryDiscount.getDiscountRules();
        this.description = categoryDiscount.getDescription();
        this.createdDate = categoryDiscount.getCreatedDate();
        this.lastUpdate = categoryDiscount.getLastUpdate();
        this.discountRuleSet = categoryDiscount.getDiscountsRules();
        if(categoryDiscount.getDiscountsRules().isEmpty()){
            this.setFilter("Не выбрано");
        } else {
            StringBuilder sb=new StringBuilder();
            for (DiscountRule discountRule: categoryDiscount.getDiscountsRules()){
                this.idOfRuleList.add(discountRule.getIdOfRule());
                sb.append(discountRule.getDescription());
                sb.append("; ");
            }
            this.setFilter(sb.substring(0, sb.length()-1));
        }

    }

    @Override
    public void completeRuleListSelection(Map<Long, String> ruleMap) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        if(null != ruleMap){
            idOfRuleList = new ArrayList<Long>();
            if(ruleMap.isEmpty()){
                filter = "Не выбрано";
            } else{
                StringBuilder stringBuilder = new StringBuilder();
                for(Long idOfRule: ruleMap.keySet()){
                    idOfRuleList.add(idOfRule);
                    stringBuilder.append(ruleMap.get(idOfRule));
                    stringBuilder.append(";");
                }
                filter = stringBuilder.toString();
                discountRules=stringBuilder.toString();
            }
        }
    }
}