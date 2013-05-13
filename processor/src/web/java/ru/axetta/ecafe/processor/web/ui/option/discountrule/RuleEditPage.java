/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
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
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RuleEditPage extends BasicWorkspacePage implements CategoryListSelectPage.CompleteHandlerList, CategoryOrgListSelectPage.CompleteHandlerList{

    private String description;
    private int priority;
    private boolean operationor;
    private String categoryDiscounts;
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private String filter = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;

    private Integer[] selectedComplexIds;

    public Integer[] getSelectedComplexIds() {
        return selectedComplexIds;
    }

    public void setSelectedComplexIds(Integer[] selectedComplexIds) {
        this.selectedComplexIds = selectedComplexIds;
    }

    public Object testCheckValues(){
        printMessage(Arrays.toString(selectedComplexIds));
        return null;
    }

    public List<SelectItem> getAvailableComplexs() {
        List<SelectItem> list = new ArrayList<SelectItem>(50);
        for (int i=0;i<50;i++) {
            SelectItem selectItem = new SelectItem(i,"Комплекс "+i);
            list.add(selectItem);
        }
        return list;
    }

    public String getIdOfCategoryOrgListString() {
        return idOfCategoryOrgList.toString().replaceAll("[^(0-9-),]","");
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]","");
    }

    public Set<CategoryDiscount> getCategoryDiscountSet() {
        return categoryDiscountSet;
    }

    public void setCategoryDiscountSet(Set<CategoryDiscount> categoryDiscountSet) {
        this.categoryDiscountSet = categoryDiscountSet;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public String getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(String categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

    public boolean isOperationor() {
        return operationor;
    }

    public void setOperationor(boolean operationor) {
        this.operationor = operationor;
    }

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

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws Exception {
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
                filter = filter.substring(0,filter.length()-1);
                categoryDiscounts=idOfCategoryList.toString();
                categoryDiscounts=categoryDiscounts.substring(1,categoryDiscounts.length()-1);
            }

        }
    }

    private String filterOrg = "Не выбрано";
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private Set<CategoryOrg> categoryOrgs;

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
        return "option/discountrule/edit";
    }

    @Transactional
    public void updateRule() throws Exception {
        entity = (DiscountRule) em.merge(entity);
        //CategoryDiscount categoryDiscount = (CategoryDiscount) persistenceSession.load(CategoryDiscount.class, this.categorydiscount.getIdOfCategory());
       // entity.setCategoryDiscount(categoryDiscount);
        entity.setDescription(description);

        List<Integer> selectedComplex = Arrays.asList(selectedComplexIds);
        entity.setComplex0(selectedComplex.contains(0)?1:0);
        entity.setComplex1(selectedComplex.contains(1) ? 1 : 0);
        entity.setComplex2(selectedComplex.contains(2) ? 1 : 0);
        entity.setComplex3(selectedComplex.contains(3) ? 1 : 0);
        entity.setComplex4(selectedComplex.contains(4) ? 1 : 0);
        entity.setComplex5(selectedComplex.contains(5) ? 1 : 0);
        entity.setComplex6(selectedComplex.contains(6) ? 1 : 0);
        entity.setComplex7(selectedComplex.contains(7) ? 1 : 0);
        entity.setComplex8(selectedComplex.contains(8) ? 1 : 0);
        entity.setComplex9(selectedComplex.contains(9) ? 1 : 0);
        DiscountRule.ComplexBuilder complexBuilder = new DiscountRule.ComplexBuilder(selectedComplex);
        entity.setComplexesMap(complexBuilder.toString());

        entity.setPriority(priority);
        entity.setOperationOr(operationor);
        //entity.setCategoryDiscounts(categoryDiscounts);
        this.categoryDiscountSet = new HashSet<CategoryDiscount>();
        entity.getCategoriesDiscounts().clear();
        //if (this.idOfCategoryList.isEmpty()) {
        //    for (CategoryDiscount categoryDiscount: entity.getCategoriesDiscounts()){
        //        this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
        //    }
        //}
        if(!this.idOfCategoryList.isEmpty()){
            List categoryList = DAOUtils.getCategoryDiscountListWithIds(em, this.idOfCategoryList);
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object: categoryList){
                this.categoryDiscountSet.add((CategoryDiscount) object);
                stringBuilder.append(((CategoryDiscount) object).getIdOfCategoryDiscount());
                stringBuilder.append(",");
            }
            entity.setCategoriesDiscounts(this.categoryDiscountSet);
            entity.setCategoryDiscounts(stringBuilder.substring(0, stringBuilder.length()-1));
        } else {
            entity.setCategoryDiscounts("");
        }

        entity.getCategoryOrgs().clear();
        if(!this.idOfCategoryOrgList.isEmpty()){
            entity.getCategoryOrgs().clear();
            List categoryOrgList = DAOUtils.getCategoryOrgWithIds(em, this.idOfCategoryOrgList);
            for (Object object: categoryOrgList){
                entity.getCategoryOrgs().add((CategoryOrg) object);
            }
        }
        em.persist(entity);
        fill(entity);
        printMessage("Данные обновлены.");
    }

    private void fill(DiscountRule discountRule) throws Exception {
        this.description = discountRule.getDescription();

        List<Integer> comls = new ArrayList<Integer>();
        if(StringUtils.isEmpty(discountRule.getComplexesMap())){
            if(discountRule.getComplex0()>0) comls.add(0);
            if(discountRule.getComplex1()>0) comls.add(1);
            if(discountRule.getComplex2()>0) comls.add(2);
            if(discountRule.getComplex3()>0) comls.add(3);
            if(discountRule.getComplex4()>0) comls.add(4);
            if(discountRule.getComplex5()>0) comls.add(5);
            if(discountRule.getComplex6()>0) comls.add(6);
            if(discountRule.getComplex7()>0) comls.add(7);
            if(discountRule.getComplex8()>0) comls.add(8);
            if(discountRule.getComplex9()>0) comls.add(9);
        } else {
            DiscountRule.ComplexBuilder complexBuilder = new DiscountRule.ComplexBuilder(discountRule.getComplexesMap());
            Map<Integer, Integer> map = complexBuilder.getMap();
            for (Integer key: map.keySet()){
                if(map.get(key)>0) comls.add(key);
            }
        }

        Integer[] temp = new Integer[comls.size()];
        this.selectedComplexIds = comls.toArray(temp);

        this.priority = discountRule.getPriority();

        this.idOfCategoryList.clear();
        if (!discountRule.getCategoriesDiscounts().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryDiscount categoryDiscount: discountRule.getCategoriesDiscounts()){
                stringBuilder.append(categoryDiscount.getCategoryName());
                stringBuilder.append("; ");
                this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
            }
            this.categoryDiscounts=stringBuilder.toString();
        }
        this.idOfCategoryOrgList.clear();
        if(!discountRule.getCategoryOrgs().isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryOrg categoryOrg: discountRule.getCategoryOrgs()){
                 stringBuilder.append(categoryOrg.getCategoryName());
                 stringBuilder.append("; ");
                 this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
            }
        }
        this.operationor=discountRule.getOperationOr();
    }

    public String getFilterOrg() {
        return filterOrg;
    }

    public void setFilterOrg(String filterOrg) {
        this.filterOrg = filterOrg;
    }

    public List<Long> getIdOfCategoryOrgList() {
        return idOfCategoryOrgList;
    }

    public void setIdOfCategoryOrgList(List<Long> idOfCategoryOrgList) {
        this.idOfCategoryOrgList = idOfCategoryOrgList;
    }

    public Set<CategoryOrg> getCategoryOrgs() {
        return categoryOrgs;
    }

    public void setCategoryOrgs(Set<CategoryOrg> categoryOrgs) {
        this.categoryOrgs = categoryOrgs;
    }

    DiscountRule entity;

    public DiscountRule getEntity() {
        return entity;
    }

    public void setEntity(DiscountRule entity) {
        this.entity = entity;
    }

    public String getEntityName() {
        return entity.getDescription();
    }

    @PersistenceContext
    EntityManager em;

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }
    public void reload() throws Exception {
        DiscountRule discountRule = em.merge(entity);

        StringBuilder categoryFilter = new StringBuilder();
        if(!discountRule.getCategoriesDiscounts().isEmpty()){
            for (CategoryDiscount categoryDiscount: discountRule.getCategoriesDiscounts()){
                this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
                categoryFilter.append(categoryDiscount.getCategoryName());
                categoryFilter.append(";");
            }
            this.filter = categoryFilter.substring(0, categoryFilter.length()-1);
        } else {
            this.filter = "Не выбрано";
        }


        StringBuilder categoryOrgFilter = new StringBuilder();
        if(!discountRule.getCategoryOrgs().isEmpty()){
            for (CategoryOrg categoryOrg: discountRule.getCategoryOrgs()){
                this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
                categoryOrgFilter.append(categoryOrg.getCategoryName());
                categoryOrgFilter.append("; ");
            }
            this.filterOrg=categoryOrgFilter.substring(0, categoryOrgFilter.length()-1);
        } else{
            this.filterOrg="Не выбрано";
        }

        fill(discountRule);
    }
}