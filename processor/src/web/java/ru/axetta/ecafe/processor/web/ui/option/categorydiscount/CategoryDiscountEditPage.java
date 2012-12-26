/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.categorydiscount;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscountEnumType;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Component
@Scope("session")
public class CategoryDiscountEditPage extends BasicWorkspacePage {
    @PersistenceContext
    EntityManager entityManager;

    public String getPageFilename() {
        return "option/categorydiscount/edit";
    }

    private long idOfCategoryDiscount;
    private String categoryName;
    private String discountRules;
    private String description;
    private Integer categoryType;
    private final CategoryDiscountEnumTypeMenu categoryDiscountEnumTypeMenu = new CategoryDiscountEnumTypeMenu();
    private String filter = "-";
    private List<Long> idOfRuleList = new ArrayList<Long>();
    private Set<DiscountRule> discountRuleSet;

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }

    public CategoryDiscountEnumTypeMenu getCategoryDiscountEnumTypeMenu() {
        return categoryDiscountEnumTypeMenu;
    }

    public String getEntityName() {
        return categoryName;
    }

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

    public String getDiscountRules() {
        return discountRules;
    }

    public void setDiscountRules(String discountRules) {
        this.discountRules = discountRules;
    }

    @Transactional
    public Object save() {
        CategoryDiscount categoryDiscount = DAOUtils.findCategoryDiscountById(entityManager, idOfCategoryDiscount);
        categoryDiscount.setIdOfCategoryDiscount(idOfCategoryDiscount);
        categoryDiscount.setCategoryName(categoryName);
        categoryDiscount.setDescription(description);
        categoryDiscount.setCategoryType(CategoryDiscountEnumType.fromInteger(categoryType));
        categoryDiscount.setLastUpdate(new Date());
        entityManager.persist(categoryDiscount);
        printMessage("Данные обновлены.");
        return null;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    @Transactional
    public void reload() {
        CategoryDiscount categoryDiscount = DAOUtils.findCategoryDiscountById(entityManager, idOfCategoryDiscount);
        this.categoryName = categoryDiscount.getCategoryName();
        this.discountRules = categoryDiscount.getDiscountRules();
        this.description = categoryDiscount.getDescription();
        this.discountRuleSet = categoryDiscount.getDiscountsRules();
        this.categoryType = categoryDiscount.getCategoryType().getValue();
        if(categoryDiscount.getDiscountsRules().isEmpty()){
            this.setFilter("-");
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

    /*@Override
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
    }    */
}