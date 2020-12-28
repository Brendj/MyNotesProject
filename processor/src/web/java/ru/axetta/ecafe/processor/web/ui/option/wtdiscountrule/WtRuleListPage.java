/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
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
public class WtRuleListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener,
        CategoryListSelectPage.CompleteHandlerList,
        CategoryOrgListSelectPage.CompleteHandlerList {

    private List<Item> items = Collections.emptyList();
    private String categoryDiscounts;
    private List<Long> idOfCategoryList = new ArrayList<>();
    private String filter = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;
    private int subCategory;
    private String filterOrg = "Не выбрано";
    private List<Long> idOfCategoryOrgList = new ArrayList<>();
    private Set<CategoryOrg> categoryOrgs;
    private Boolean showArchived = false;

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        try {
            WtDiscountRule discountRule = (WtDiscountRule) DAOService.getInstance().detachEntity(confirmDeletePage.getEntity());
            discountRule.setDeletedState(true);
            DAOService.getInstance().mergeEntity(discountRule);
            onShow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPageFilename() {
        return "option/wtdiscountrule/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    @Override
    public void onShow() {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public Object updatePage() throws Exception {
        onShow();
        return this;
    }

    public Object clearPageFilter(){
        idOfCategoryList = new ArrayList<Long>();
        filter = "Не выбрано";
        subCategory = 0;
        filterOrg = "Не выбрано";
        idOfCategoryOrgList = new ArrayList<>();
        showArchived = false;
        return this;
    }

    @Transactional
    public void reload() {

        List<Item> items = new ArrayList<>();
        List<WtDiscountRule> wtDiscountRuleList = DAOUtils.listWtDiscountRules(em, showArchived);

        for (WtDiscountRule wtDiscountRule : wtDiscountRuleList) {

            List<Long> categoriesDiscountsIds = new ArrayList<>();
            for (CategoryDiscount categoryDiscount : wtDiscountRule.getCategoryDiscounts()) {
                categoriesDiscountsIds.add(categoryDiscount.getIdOfCategoryDiscount());
            }
            if (!idOfCategoryList.isEmpty() && Collections.disjoint(idOfCategoryList, categoriesDiscountsIds)) {
                continue;
            }

            List<Long> categoriesDiscountsOrgIds = new ArrayList<>();
            for (CategoryOrg categoryOrg : wtDiscountRule.getCategoryOrgs()) {
                categoriesDiscountsOrgIds.add(categoryOrg.getIdOfCategoryOrg());
            }

            if (!idOfCategoryOrgList.isEmpty() && Collections
                    .disjoint(idOfCategoryOrgList, categoriesDiscountsOrgIds)) {
                continue;
            }

            if (wtDiscountRule.getSubCategory() != null && WtRuleCreatePage.SUB_CATEGORIES[subCategory] != null){
                if (subCategory != 0 && !wtDiscountRule.getSubCategory()
                        .equals(WtRuleCreatePage.SUB_CATEGORIES[subCategory])) {
                    continue;
                }
            }

            Item item = new Item(wtDiscountRule);

            if (!wtDiscountRule.getCategoryDiscounts().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryDiscount categoryDiscount : wtDiscountRule.getCategoryDiscounts()) {
                    stringBuilder.append(categoryDiscount.getCategoryName());
                    stringBuilder.append("; ");
                }
                item.setCategoryDiscounts(stringBuilder.substring(0, stringBuilder.length() - 1));
            }
            item.setWtEntity(wtDiscountRule);

            if (!wtDiscountRule.getCategoryOrgs().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryOrg categoryOrg : wtDiscountRule.getCategoryOrgs()) {
                    stringBuilder.append(categoryOrg.getCategoryName());
                    stringBuilder.append(";");
                }
                item.setCategoryOrgs(stringBuilder.substring(0, stringBuilder.length() - 1));
            }
            items.add(item);
        }

        this.items = items;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        if (null != categoryMap) {
            idOfCategoryList = new ArrayList<Long>();
            if (categoryMap.isEmpty()) {
                filter = "Не выбрано";
            } else {
                filter = "";
                for (Long idOfCategory : categoryMap.keySet()) {
                    idOfCategoryList.add(idOfCategory);
                    filter = filter.concat(categoryMap.get(idOfCategory) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
                categoryDiscounts = idOfCategoryList.toString();
                categoryDiscounts = categoryDiscounts.substring(1, categoryDiscounts.length() - 1);
            }

        }
    }

    public void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
        if (null != categoryOrgMap) {
            idOfCategoryOrgList = new ArrayList<Long>();
            if (categoryOrgMap.isEmpty()) {
                filterOrg = "Не выбрано";

            } else {
                filterOrg = "";
                for (Long idOfCategoryOrg : categoryOrgMap.keySet()) {
                    idOfCategoryOrgList.add(idOfCategoryOrg);
                    filterOrg = filterOrg.concat(categoryOrgMap.get(idOfCategoryOrg) + "; ");
                }
                filterOrg = filterOrg.substring(0, filterOrg.length() - 1);
            }
        }
    }

    public List<SelectItem> getSubCategories() {
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (int i = 0; i < WtRuleCreatePage.SUB_CATEGORIES.length; i++) {
            String group = WtRuleCreatePage.SUB_CATEGORIES[i];
            res.add(new SelectItem(i, group));
        }
        return res;
    }

    public List<Item> getItems() {
        return items;
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

    public Boolean getShowArchived() {
        return showArchived;
    }

    public void setShowArchived(Boolean showArchived) {
        this.showArchived = showArchived;
    }

    public String getIdOfCategoryOrgListString() {
        return idOfCategoryOrgList.toString().replaceAll("[^(0-9-),]", "");
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]", "");
    }


    /// class Item ///
    public static class Item {

        WtDiscountRule wtEntity;

        private long idOfRule;
        private String description;
        private boolean operationor;
        private String categoryDiscounts;
        private String CategoryOrgs;
        private List<CategoryDiscount> categoryDiscountList;
        private List<CategoryOrg> categoryOrgList;
        private String subCategory;
        private String codeMSP;
        private int priority;

        public WtDiscountRule getWtEntity() {
            return wtEntity;
        }

        public String getCodeMSP() {
            return codeMSP;
        }

        public void setCodeMSP(String codeMSP) {
            this.codeMSP = codeMSP;
        }

        public void setWtEntity(WtDiscountRule wtEntity) {
            this.wtEntity = wtEntity;
        }

        public long getIdOfRule() {
            return idOfRule;
        }

        public String getDescription() {
            return description;
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

        public void setIdOfRule(long idOfRule) {
            this.idOfRule = idOfRule;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isOperationor() {
            return operationor;
        }

        public void setOperationor(boolean operationor) {
            this.operationor = operationor;
        }

        public String getSubCategory() {
            return subCategory;
        }

        public void setSubCategory(String subCategory) {
            this.subCategory = subCategory;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public Item(WtDiscountRule wtDiscountRule) {

            this.idOfRule = wtDiscountRule.getIdOfRule();
            this.description = wtDiscountRule.getDescription();
            this.priority = wtDiscountRule.getPriority();
            this.operationor = wtDiscountRule.isOperationOr();
            this.codeMSP = wtDiscountRule.getCodeMSP() == null ? "" : wtDiscountRule.getCodeMSP().getCode().toString();

            this.categoryDiscountList = new LinkedList<>();
            if (!wtDiscountRule.getCategoryDiscounts().isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (CategoryDiscount categoryDiscount : wtDiscountRule.getCategoryDiscounts()) {
                    this.categoryDiscountList.add(categoryDiscount);
                    stringBuilder.append(categoryDiscount.getCategoryName());
                    stringBuilder.append(",");
                }
                this.categoryDiscounts = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
            this.categoryOrgList = new LinkedList<>();
            if (!wtDiscountRule.getCategoryOrgs().isEmpty()) {
                this.categoryOrgList.addAll(wtDiscountRule.getCategoryOrgs());
            }
            subCategory = wtDiscountRule.getSubCategory();
        }
    }
}
