/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtDiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

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
 * Time: 9:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RuleListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener,
        CategoryListSelectPage.CompleteHandlerList, CategoryOrgListSelectPage.CompleteHandlerList {
    private List<Item> items = Collections.emptyList();
    private String categoryDiscounts;
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private String filter = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;
    private int subCategory;
    private String filterOrg = "Не выбрано";
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private Set<CategoryOrg> categoryOrgs;

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        DAOService.getInstance().deleteEntity(confirmDeletePage.getEntity());
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public String getPageFilename() {
        return "option/discountrule/list";
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public Object updatePage() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
        return this;
    }

    public Object clearPageFilter() throws Exception {
        idOfCategoryList = new ArrayList<Long>();
        filter = "Не выбрано";
        subCategory = 0;
        filterOrg = "Не выбрано";
        idOfCategoryOrgList = new ArrayList<Long>();
        RuntimeContext.getAppContext().getBean(getClass()).reload();
        return this;
    }

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Transactional
    public void reload() {
        List<Item> items = new ArrayList<>();

        // Old
        List<DiscountRule> discountRuleList = DAOUtils.listDiscountRules(em);

        for (DiscountRule discountRule : discountRuleList) {

            List<Long> categoriesDiscountsIds = new ArrayList<Long>();
            for(CategoryDiscount categoryDiscount : discountRule.getCategoriesDiscounts()){
                categoriesDiscountsIds.add(categoryDiscount.getIdOfCategoryDiscount());
            }
            if(!idOfCategoryList.isEmpty() && Collections.disjoint(idOfCategoryList, categoriesDiscountsIds)){
                continue;
            }

            List<Long> categoriesDiscountsOrgIds = new ArrayList<Long>();
            for(CategoryOrg categoryOrg : discountRule.getCategoryOrgs()){
                categoriesDiscountsOrgIds.add(categoryOrg.getIdOfCategoryOrg());
            }
            if(!idOfCategoryOrgList.isEmpty() && Collections.disjoint(idOfCategoryOrgList, categoriesDiscountsOrgIds)){
                continue;
            }

            if(subCategory != 0 && !discountRule.getSubCategory().equals(RuleCreatePage.SUB_CATEGORIES[subCategory])){
                continue;
            }

            Item item = new Item(discountRule);

            if (!discountRule.getCategoriesDiscounts().isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryDiscount categoryDiscount: discountRule.getCategoriesDiscounts()){
                    stringBuilder.append(categoryDiscount.getCategoryName());
                    stringBuilder.append("; ");
                }
                item.setCategoryDiscounts(stringBuilder.substring(0, stringBuilder.length()-1));
            }
            item.setEntity(discountRule);

            if(!discountRule.getCategoryOrgs().isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for(CategoryOrg categoryOrg: discountRule.getCategoryOrgs()){
                    stringBuilder.append(categoryOrg.getCategoryName());
                    stringBuilder.append(";");
                }
                item.setCategoryOrgs(stringBuilder.substring(0, stringBuilder.length()-1));
            }

            items.add(item);
        }

        //wt
        List<WtDiscountRule> wtDiscountRuleList = DAOUtils.listWtDiscountRules(em);

        for (WtDiscountRule wtDiscountRule : wtDiscountRuleList) {

            List<Long> categoriesDiscountsIds = new ArrayList<>();
            for(CategoryDiscount categoryDiscount : wtDiscountRule.getCategoryDiscounts()){
                categoriesDiscountsIds.add(categoryDiscount.getIdOfCategoryDiscount());
            }
            if(!idOfCategoryList.isEmpty() && Collections.disjoint(idOfCategoryList, categoriesDiscountsIds)){
                continue;
            }

            List<Long> categoriesDiscountsOrgIds = new ArrayList<>();
            for(CategoryOrg categoryOrg : wtDiscountRule.getCategoryOrgs()){
                categoriesDiscountsOrgIds.add(categoryOrg.getIdOfCategoryOrg());
            }
            if(!idOfCategoryOrgList.isEmpty() && Collections.disjoint(idOfCategoryOrgList, categoriesDiscountsOrgIds)){
                continue;
            }

            //if(subCategory != 0 && !wtDiscountRule.getSubCategory().equals(RuleCreatePage.SUB_CATEGORIES[subCategory])){
            //    continue;
            //}

            Item item = new Item(wtDiscountRule);

            if (!wtDiscountRule.getCategoryDiscounts().isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryDiscount categoryDiscount: wtDiscountRule.getCategoryDiscounts()){
                    stringBuilder.append(categoryDiscount.getCategoryName());
                    stringBuilder.append("; ");
                }
                item.setCategoryDiscounts(stringBuilder.substring(0, stringBuilder.length()-1));
            }

            item.setWtEntity(wtDiscountRule);

            if(!wtDiscountRule.getCategoryOrgs().isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for(CategoryOrg categoryOrg: wtDiscountRule.getCategoryOrgs()){
                    stringBuilder.append(categoryOrg.getCategoryName());
                    stringBuilder.append(";");
                }
                item.setCategoryOrgs(stringBuilder.substring(0, stringBuilder.length()-1));
            }

            items.add(item);
        }

        this.items = items;

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

    public List<SelectItem> getSubCategories() throws Exception {
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (int i=0; i<RuleCreatePage.SUB_CATEGORIES.length; i++) {
            String group = RuleCreatePage.SUB_CATEGORIES[i];
            res.add(new SelectItem(i, group));
        }
        return res;
    }

    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public void setIdOfCategoryList(List<Long> idOfCategoryList) {
        this.idOfCategoryList = idOfCategoryList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public String getIdOfCategoryOrgListString() {
        return idOfCategoryOrgList.toString().replaceAll("[^(0-9-),]","");
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]","");
    }

    public static class Item {
        DiscountRule entity;
        WtDiscountRule wtEntity;
        private long idOfRule;
        private String description;
        private int complex0;
        private int complex1;
        private int complex2;
        private int complex3;
        private int complex4;
        private int complex5;
        private int complex6;
        private int complex7;
        private int complex8;
        private int complex9;
        private boolean operationor;
        private String categoryDiscounts;
        private String CategoryOrgs;
        private List<CategoryDiscount> categoryDiscountList;
        private List<CategoryOrg> categoryOrgList;
        private String subCategory;
        //


        public DiscountRule getEntity() {
            return entity;
        }

        public void setEntity(DiscountRule entity) {
            this.entity = entity;
        }

        public WtDiscountRule getWtEntity() {
            return wtEntity;
        }

        public void setWtEntity(WtDiscountRule wtEntity) {
            this.wtEntity = wtEntity;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public String getCategoryOrgs() {
            return CategoryOrgs;
        }

        public void setCategoryOrgs(String categoryOrgs) {
            CategoryOrgs = categoryOrgs;
        }

        public List<CategoryOrg> getCategoryOrgList() {
            return categoryOrgList;
        }

        public void setCategoryOrgList(List<CategoryOrg> categoryOrgList) {
            this.categoryOrgList = categoryOrgList;
        }

        public List<CategoryDiscount> getCategoryDiscountList() {
            return categoryDiscountList;
        }

        public void setCategoryDiscountList(List<CategoryDiscount> categoryDiscountList) {
            this.categoryDiscountList = categoryDiscountList;
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

        private int priority;
        //

        public Item(DiscountRule discountRule) {
            this.idOfRule = discountRule.getIdOfRule();
            this.description = discountRule.getDescription();
            this.complex0 = discountRule.getComplex0();
            this.complex1 = discountRule.getComplex1();
            this.complex2 = discountRule.getComplex2();
            this.complex3 = discountRule.getComplex3();
            this.complex4 = discountRule.getComplex4();
            this.complex5 = discountRule.getComplex5();
            this.complex6 = discountRule.getComplex6();
            this.complex7 = discountRule.getComplex7();
            this.complex8 = discountRule.getComplex8();
            this.complex9 = discountRule.getComplex9();
            this.priority = discountRule.getPriority();
            this.operationor = discountRule.getOperationOr();
            this.categoryDiscountList = new LinkedList<CategoryDiscount>();
            if(!discountRule.getCategoriesDiscounts().isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryDiscount categoryDiscount: discountRule.getCategoriesDiscounts()){
                    this.categoryDiscountList.add(categoryDiscount);
                    stringBuilder.append(categoryDiscount.getCategoryName());
                    stringBuilder.append(",");
                }
                this.categoryDiscounts = stringBuilder.substring(0, stringBuilder.length()-1);
            }

            this.categoryOrgList = new LinkedList<CategoryOrg>();
            if(!discountRule.getCategoryOrgs().isEmpty()){
                this.categoryOrgList.addAll(discountRule.getCategoryOrgs());
            }
            subCategory = discountRule.getSubCategory();
        }

        public Item(WtDiscountRule wtDiscountRule) {
            this.idOfRule = wtDiscountRule.getIdOfRule();
            this.description = wtDiscountRule.getDescription();
            this.priority = wtDiscountRule.getPriority();
            this.operationor = wtDiscountRule.isOperationOr();
            this.categoryDiscountList = new LinkedList<>();
            if(!wtDiscountRule.getCategoryDiscounts().isEmpty()){
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryDiscount categoryDiscount: wtDiscountRule.getCategoryDiscounts()){
                    this.categoryDiscountList.add(categoryDiscount);
                    stringBuilder.append(categoryDiscount.getCategoryName());
                    stringBuilder.append(",");
                }
                this.categoryDiscounts = stringBuilder.substring(0, stringBuilder.length()-1);
            }

            this.categoryOrgList = new LinkedList<CategoryOrg>();
            if(!wtDiscountRule.getCategoryOrgs().isEmpty()){
                this.categoryOrgList.addAll(wtDiscountRule.getCategoryOrgs());
            }
            subCategory = wtDiscountRule.getSubCategory();
        }

        public long getIdOfRule() {
            return idOfRule;
        }

        public String getDescription() {
            return description;
        }

        public int getComplex0() {
            return complex0;
        }

        public int getComplex1() {
            return complex1;
        }

        public int getComplex2() {
            return complex2;
        }

        public int getComplex3() {
            return complex3;
        }

        public int getComplex4() {
            return complex4;
        }

        public int getComplex5() {
            return complex5;
        }

        public int getComplex6() {
            return complex6;
        }

        public int getComplex7() {
            return complex7;
        }

        public int getComplex8() {
            return complex8;
        }

        public int getComplex9() {
            return complex9;
        }

    }
}
