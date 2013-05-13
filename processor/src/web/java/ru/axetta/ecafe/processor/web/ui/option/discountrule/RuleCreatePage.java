/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RuleCreatePage extends BasicWorkspacePage
        implements CategoryListSelectPage.CompleteHandlerList, CategoryOrgListSelectPage.CompleteHandlerList {

    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private String description;
    private Boolean operationOr;
    private String categoryDiscounts;
    private String filter = "Не выбрано";
    private String filterOrg = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;
    private Set<CategoryOrg> categoryOrgs;

    private Integer[] selectedComplexIds;

    public Integer[] getSelectedComplexIds() {
        return selectedComplexIds;
    }

    public void setSelectedComplexIds(Integer[] selectedComplexIds) {
        this.selectedComplexIds = selectedComplexIds;
    }

    public List<SelectItem> getAvailableComplexs() {
        List<SelectItem> list = new ArrayList<SelectItem>(50);
        for (int i=0;i<50;i++) {
            SelectItem selectItem = new SelectItem(i,"Комплекс "+i);
            list.add(selectItem);
        }
        return list;
    }

    public String getFilterOrg() {
        return filterOrg;
    }

    public void setFilterOrg(String filterOrg) {
        this.filterOrg = filterOrg;
    }

    public Set<CategoryOrg> getCategoryOrgs() {
        return categoryOrgs;
    }

    public void setCategoryOrgs(Set<CategoryOrg> categoryOrgs) {
        this.categoryOrgs = categoryOrgs;
    }

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public List<Long> getIdOfCategoryOrgList(){
        return idOfCategoryOrgList;
    }

    public String getFilter() {
        return filter;
    }

    public String getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(String categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

    public Boolean getOperationOr() {
        return operationOr;
    }

    public void setOperationOr(Boolean operationOr) {
        this.operationOr = operationOr;
    }

    private int priority;
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
        //To change body of implemented methods use File | Settings | File Templates.
         if(null != categoryMap) {
             idOfCategoryList = new ArrayList<Long>();
             if(categoryMap.isEmpty()){
                  filter = "Не выбрано";
             } else {
                 filter="";
                 for(Long idOfCategory: categoryMap.keySet()){
                     idOfCategoryList.add(idOfCategory);
                     filter=filter.concat(categoryMap.get(idOfCategory)+ "; ");
                 }
                 filter = filter.substring(0,filter.length()-2);
                 categoryDiscounts=idOfCategoryList.toString();
                 categoryDiscounts=categoryDiscounts.substring(1,categoryDiscounts.length()-1);

             }

         }
    }

    public void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        if(null != categoryOrgMap) {
            idOfCategoryOrgList = new ArrayList<Long>();
            if(categoryOrgMap.isEmpty()){
                filterOrg = "Не выбрано";
            } else {
                filterOrg="";
                for(Long idOfCategoryOrg: categoryOrgMap.keySet()){
                    idOfCategoryOrgList.add(idOfCategoryOrg);
                    filterOrg=filterOrg.concat(categoryOrgMap.get(idOfCategoryOrg)+ "; ");
                }
                filterOrg = filterOrg.substring(0,filterOrg.length()-1);
            }

        }
    }

    public String getPageFilename() {
        return "option/discountrule/create";
    }

    @Override
    public void onShow() throws Exception {
        this.description = "";
        this.priority = 0;
        this.categoryDiscounts = "";
        this.operationOr=false;
        this.filter="Не выбрано";
        this.filterOrg="Не выбрано";
    }

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void createRule() throws Exception {
//        CategoryDiscount categorydiscount = (CategoryDiscount) session.load(CategoryDiscount.class, this.categorydiscount.getIdOfCategory());
        DiscountRule discountRule = new DiscountRule();
        discountRule.setDescription(description);
        List<Integer> selectedComplex = Arrays.asList(selectedComplexIds);
        discountRule.setComplex0(selectedComplex.contains(0)?1:0);
        discountRule.setComplex1(selectedComplex.contains(1) ? 1 : 0);
        discountRule.setComplex2(selectedComplex.contains(2) ? 1 : 0);
        discountRule.setComplex3(selectedComplex.contains(3) ? 1 : 0);
        discountRule.setComplex4(selectedComplex.contains(4) ? 1 : 0);
        discountRule.setComplex5(selectedComplex.contains(5) ? 1 : 0);
        discountRule.setComplex6(selectedComplex.contains(6) ? 1 : 0);
        discountRule.setComplex7(selectedComplex.contains(7) ? 1 : 0);
        discountRule.setComplex8(selectedComplex.contains(8) ? 1 : 0);
        discountRule.setComplex9(selectedComplex.contains(9) ? 1 : 0);
        DiscountRule.ComplexBuilder complexBuilder = new DiscountRule.ComplexBuilder(selectedComplex);
        discountRule.setComplexesMap(complexBuilder.toString());
        discountRule.setPriority(priority);
        discountRule.setOperationOr(operationOr);
        discountRule.setCategoryDiscounts(categoryDiscounts);
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        if (!this.idOfCategoryList.isEmpty()) {
            List categoryList = DAOUtils.getCategoryDiscountListWithIds(em, this.idOfCategoryList);
            for (Object object: categoryList){
                 this.categoryDiscountSet.add((CategoryDiscount) object);
            }
            discountRule.setCategoriesDiscounts(this.categoryDiscountSet);
        }
        if (!this.idOfCategoryOrgList.isEmpty()) {
            List categoryOrgList = DAOUtils.getCategoryOrgWithIds(em, this.idOfCategoryOrgList);
            for (Object object: categoryOrgList){
                discountRule.getCategoryOrgs().add((CategoryOrg) object);
            }
        }

        em.persist(discountRule);
        printMessage("Правило зарегистрировано успешно");
    }
}
