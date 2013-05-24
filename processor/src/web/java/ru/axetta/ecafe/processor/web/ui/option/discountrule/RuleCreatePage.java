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
        discountRule.setComplex10(selectedComplex.contains(10) ? 1 : 0);
        discountRule.setComplex11(selectedComplex.contains(11) ? 1 : 0);
        discountRule.setComplex12(selectedComplex.contains(12) ? 1 : 0);
        discountRule.setComplex13(selectedComplex.contains(13) ? 1 : 0);
        discountRule.setComplex14(selectedComplex.contains(14) ? 1 : 0);
        discountRule.setComplex15(selectedComplex.contains(15) ? 1 : 0);
        discountRule.setComplex16(selectedComplex.contains(16) ? 1 : 0);
        discountRule.setComplex17(selectedComplex.contains(17) ? 1 : 0);
        discountRule.setComplex18(selectedComplex.contains(18) ? 1 : 0);
        discountRule.setComplex19(selectedComplex.contains(19) ? 1 : 0);
        discountRule.setComplex20(selectedComplex.contains(20) ? 1 : 0);
        discountRule.setComplex21(selectedComplex.contains(21) ? 1 : 0);
        discountRule.setComplex22(selectedComplex.contains(22) ? 1 : 0);
        discountRule.setComplex23(selectedComplex.contains(23) ? 1 : 0);
        discountRule.setComplex24(selectedComplex.contains(24) ? 1 : 0);
        discountRule.setComplex25(selectedComplex.contains(25) ? 1 : 0);
        discountRule.setComplex26(selectedComplex.contains(26) ? 1 : 0);
        discountRule.setComplex27(selectedComplex.contains(27) ? 1 : 0);
        discountRule.setComplex28(selectedComplex.contains(28) ? 1 : 0);
        discountRule.setComplex29(selectedComplex.contains(29) ? 1 : 0);
        discountRule.setComplex30(selectedComplex.contains(30) ? 1 : 0);
        discountRule.setComplex31(selectedComplex.contains(31) ? 1 : 0);
        discountRule.setComplex32(selectedComplex.contains(32) ? 1 : 0);
        discountRule.setComplex33(selectedComplex.contains(33) ? 1 : 0);
        discountRule.setComplex34(selectedComplex.contains(34) ? 1 : 0);
        discountRule.setComplex35(selectedComplex.contains(35) ? 1 : 0);
        discountRule.setComplex36(selectedComplex.contains(36) ? 1 : 0);
        discountRule.setComplex37(selectedComplex.contains(37) ? 1 : 0);
        discountRule.setComplex38(selectedComplex.contains(38) ? 1 : 0);
        discountRule.setComplex39(selectedComplex.contains(39) ? 1 : 0);
        discountRule.setComplex40(selectedComplex.contains(40) ? 1 : 0);
        discountRule.setComplex41(selectedComplex.contains(41) ? 1 : 0);
        discountRule.setComplex42(selectedComplex.contains(42) ? 1 : 0);
        discountRule.setComplex43(selectedComplex.contains(43) ? 1 : 0);
        discountRule.setComplex44(selectedComplex.contains(44) ? 1 : 0);
        discountRule.setComplex45(selectedComplex.contains(45) ? 1 : 0);
        discountRule.setComplex46(selectedComplex.contains(46) ? 1 : 0);
        discountRule.setComplex47(selectedComplex.contains(47) ? 1 : 0);
        discountRule.setComplex48(selectedComplex.contains(48) ? 1 : 0);
        discountRule.setComplex49(selectedComplex.contains(49) ? 1 : 0);
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
