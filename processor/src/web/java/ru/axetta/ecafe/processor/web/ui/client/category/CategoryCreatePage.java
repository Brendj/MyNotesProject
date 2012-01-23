/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.category;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.management.relation.Relation;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
public class CategoryCreatePage extends BasicWorkspacePage {
    public String getPageFilename() {
        return "client/category/create";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String description;
    private Date createdDate;
    private Date lastUpdate = new Date();
    private String discountRules;
    private String filter = "Не выбрано";
    private List<Long> idOfRuleList = new ArrayList<Long>();

    public List<Long> getIdOfRuleList() {
        return idOfRuleList;
    }

    public String getFilter() {
        return filter;
    }

    public String getDiscountRules() {
        return discountRules;
    }

    public void setDiscountRules(String discountRules) {
        this.discountRules = discountRules;
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

    public void completeRuleListSelection(Map<Long, String> ruleMap) throws HibernateException {
        if(null != ruleMap){
            idOfRuleList = new ArrayList<Long>();
            if(ruleMap.isEmpty()){
                filter = "Не выбрано";
            } else{
                filter="";
                for(Long idOfRule: ruleMap.keySet()){
                    idOfRuleList.add(idOfRule);
                    filter=filter.concat(ruleMap.get(idOfRule)+ "; ");
                }
                filter = filter.substring(0,filter.length()-2);
                discountRules=idOfRuleList.toString();
                discountRules=discountRules.substring(1,discountRules.length()-1);
            }
        }
    }
    
    public void fill(Session session) throws Exception {

    }

    public void createCategory(Session session) throws Exception {
        int size=session.createCriteria(CategoryDiscount.class).add(
                Restrictions.eq("idOfCategoryDiscount", idOfCategoryDiscount)
        ).list().size();
        if (size==0){
            CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, discountRules, description,
                    createdDate, lastUpdate);
            session.save(categoryDiscount);
        } else throw new Exception("This category is exists.");
    }
}
