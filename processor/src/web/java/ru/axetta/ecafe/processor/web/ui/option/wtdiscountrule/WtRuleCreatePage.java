/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class WtRuleCreatePage extends BasicWorkspacePage implements CategoryListSelectPage.CompleteHandlerList,
        CategoryOrgListSelectPage.CompleteHandlerList, ContragentListSelectPage.CompleteHandler, OrgListSelectPage.CompleteHandlerList {

    public static final String SUB_CATEGORIES[] = new String[]{
            "", "Обучающиеся из многодетных семей 5-9 кл. (завтрак+обед)",
            "Обучающиеся из многодетных семей 10-11 кл. (завтрак+обед)", "Обучающиеся 5-9 кл.(завтрак+обед)",
            "Обучающиеся 10-11 кл.(завтрак+обед)", "Обучающиеся 1-4 кл. (завтрак)",
            "Обучающиеся из соц. незащищ. семей 1-4 кл. (завтрак+обед)",
            //  Если измениться, необходимо поменять DailyReferReort.getReportData : 353
            "Обучающиеся из соц. незащищ. семей 5-9 кл. (завтрак+обед)",
            //  Если измениться, необходимо поменять DailyReferReort.getReportData : 354
            "Обучающиеся из соц. незащищ. семей 10-11 кл. (завтрак+обед)",
            //  Если измениться, необходимо поменять DailyReferReort.getReportData : 354
            "Обучающиеся из многодетных семей 1-4 кл. (завтрак+обед)", "Обучающиеся 1-4 кл.(завтрак+обед)",

            "Обучающиеся 1,5-3 лет (завтрак 1+завтрак 2+обед+упл. полдник)",
            "Обучающиеся 3-7 лет (завтрак 1+завтрак 2+обед+упл. полдник)",
            "Обучающиеся 1,5-3 лет (завтрак 1+завтрак 2+обед+полдник+ужин 1+ужин 2)",
            "Обучающиеся 3-7 лет (завтрак 1+завтрак 2+обед+полдник+ужин 1+ужин 2)",

            "Начальные классы 1-4 (завтрак + обед + полдник)",
            "Средние и  старшие классы 5-9 (завтрак + обед + полдник)",
            "Средние и  старшие классы 10-11 (завтрак + обед + полдник)"};
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private String description;
    private Boolean operationOr;
    private String categoryDiscounts;
    private String filter = "Не выбрано";
    private String filterOrg = "Не выбрано";
    private int priority;
    private Integer discountRate;
    private int subCategory = -1;
    @Autowired
    private DAOService daoService;

    private long complexType = -1L;
    private long ageGroup = -1L;
    private long dietType = -1L;

    private List<WtSelectedComplex> wtSelectedComplexes = new ArrayList<>();

    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private List<ContragentItem> contragentItems = new ArrayList<>();

    private String orgListFilter;
    private List<Long> idOfOrgList = new ArrayList<>();

    public List<SelectItem> getSubCategories() throws Exception {
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (int i = 0; i < SUB_CATEGORIES.length; i++) {
            String group = SUB_CATEGORIES[i];
            res.add(new SelectItem(i, group));
        }
        return res;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
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
                filter = filter.substring(0, filter.length() - 2);
                categoryDiscounts = idOfCategoryList.toString();
                categoryDiscounts = categoryDiscounts.substring(1, categoryDiscounts.length() - 1);
            }
        }
    }

    public void completeCategoryOrgListSelection(Map<Long, String> categoryOrgMap) throws Exception {
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

    public String getPageFilename() {
        return "option/wtdiscountrule/create";
    }

    public List<SelectItem> getComplexTypes() {
        List<SelectItem> res = new ArrayList<>();
        List<WtComplexGroupItem> complexGroupItems;
        res.add(new SelectItem(0, " "));
        complexGroupItems = daoService.getWtComplexGroupList();
        Long valueAllId = daoService.getWtComplexGroupIdByDescription("все");
        for (WtComplexGroupItem item : complexGroupItems) {
            if (valueAllId > 0 && !item.getIdOfComplexGroupItem().equals(valueAllId)) {
                res.add(new SelectItem(item.getIdOfComplexGroupItem(), item.getDescription()));
            }
        }
        return res;
    }

    public List<SelectItem> getAgeGroups() {
        List<SelectItem> res = new ArrayList<>();
        List<WtAgeGroupItem> ageGroupItems;
        res.add(new SelectItem(0, " "));
        ageGroupItems = daoService.getWtAgeGroupList();
        Long valueAllId = daoService.getWtAgeGroupIdByDescription("все");
        int i = 0;
        for (WtAgeGroupItem item : ageGroupItems) {
            if (valueAllId > 0 && !item.getIdOfAgeGroupItem().equals(valueAllId)) {
                res.add(new SelectItem(++i, item.getDescription()));
            }
        }
        return res;
    }

    public List<SelectItem> getDietTypes() {
        List<SelectItem> res = new ArrayList<>();
        List<WtDietType> dietTypeItems;
        res.add(new SelectItem(0, " "));
        dietTypeItems = daoService.getWtDietTypeList();
        for (WtDietType item : dietTypeItems) {
            res.add(new SelectItem(item.getIdOfDietType(), item.getDescription()));
        }
        return res;
    }

    @Override
    public void completeContragentListSelection(Session session, List<Long> idOfContragentList, int multiContrFlag,
            String classTypes) throws Exception {
        contragentItems.clear();
        for (Long idOfContragent : idOfContragentList) {
            Contragent currentContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            ContragentItem contragentItem = new ContragentItem(currentContragent);
            contragentItems.add(contragentItem);
        }
        setContragentFilterInfo(contragentItems);
    }

    private void setContragentFilterInfo(List<ContragentItem> contragentItems) {
        StringBuilder str = new StringBuilder();
        StringBuilder ids = new StringBuilder();
        if (contragentItems.isEmpty()) {
            contragentFilter = "Не выбрано";
        } else {
            for (ContragentItem it : contragentItems) {
                if (str.length() > 0) {
                    str.append("; ");
                    ids.append(",");
                }
                str.append(it.getContragentName());
                ids.append(it.getIdOfContragent());
            }
            contragentFilter = str.toString();
        }
        contragentIds = ids.toString();
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            if (orgMap.isEmpty()) {
                orgListFilter = "Не выбрано";
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                for (Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                }
                orgListFilter = stringBuilder.substring(0, stringBuilder.length() - 2);
            }
        }
    }

    public void fillWtSelectedComplexes() {
        wtSelectedComplexes.clear();
        if (isComplexFilterEmpty()) {
            List<WtComplex> wtComplexes = daoService.getWtComplexesList();
            for (WtComplex wtComplex : wtComplexes) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex, false));
            }
        } else {
            List<Long> wtComplexGroupIds = new ArrayList<>();
            List<Long> wtAgeGroupIds = new ArrayList<>();
            List<Long> contragentIdList = new ArrayList<>();

            if (!contragentItems.isEmpty()) {
                for (ContragentItem contragentItem : contragentItems) {
                    contragentIdList.add(contragentItem.idOfContragent);
                }
            }

            if (complexType > 0) {
                wtComplexGroupIds.add(complexType);
                Long valueAllId = daoService.getWtComplexGroupIdByDescription("все");
                if (valueAllId > 0) {
                    wtComplexGroupIds.add(valueAllId);
                }
            }
            if (ageGroup > 0) {
                wtAgeGroupIds.add(ageGroup);
                Long valueAllId = daoService.getWtAgeGroupIdByDescription("все");
                if (valueAllId > 0) {
                    wtAgeGroupIds.add(valueAllId);
                }
            }

            List<WtComplex> wtComplexes = daoService.getWtComplexListByFilter(wtComplexGroupIds, wtAgeGroupIds,
                    dietType, contragentIdList, idOfOrgList, null);

            for (WtComplex wtComplex : wtComplexes) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex, false));
            }
        }
    }

    private boolean isComplexFilterEmpty() {
        return complexType == 0 && ageGroup == 0 && dietType == 0 && contragentItems.isEmpty();
    }

    @Override
    public void onShow() throws Exception {
        this.description = "";
        this.priority = 0;
        this.discountRate = 100;
        this.categoryDiscounts = "";
        this.operationOr = false;
        this.filter = "Не выбрано";
        this.filterOrg = "Не выбрано";
        this.subCategory = -1;
    }

    @Transactional
    public void createRule() throws Exception {

        WtDiscountRule wtDiscountRule = new WtDiscountRule();
        String strSubCategory = "";
        if (this.subCategory > 0) {
            strSubCategory = SUB_CATEGORIES[this.subCategory];
        }

        if (StringUtils.isNotEmpty(strSubCategory)) {
            wtDiscountRule.setSubCategory(strSubCategory);
        }

        wtDiscountRule.setSubCategory(strSubCategory);
        wtDiscountRule.setDescription(description);

        List<WtComplex> ruleComplexes = new ArrayList<>();
        for (WtSelectedComplex wtSelectedComplex : wtSelectedComplexes) {
            if (wtSelectedComplex.isChecked()) {
                WtComplex complex = wtSelectedComplex.getWtComplex();
                ruleComplexes.add(complex);
            }
        }
        wtDiscountRule.setComplexes(new HashSet<>(ruleComplexes));

        wtDiscountRule.setOperationOr(operationOr);
        wtDiscountRule.setPriority(priority);
        wtDiscountRule.setRate(discountRate);
        wtDiscountRule.setDeletedState(false);

        Set<CategoryDiscount> categoryDiscountSet = new HashSet<>();

        if (!this.idOfCategoryList.isEmpty()) {
            List<CategoryDiscount> categoryList = daoService.getCategoryDiscountListWithIds(this.idOfCategoryList);
            categoryDiscountSet.addAll(categoryList);
            wtDiscountRule.setCategoryDiscounts(categoryDiscountSet);
        }

        if (!this.idOfCategoryOrgList.isEmpty()) {
            List<CategoryOrg> categoryOrgList = daoService.getCategoryOrgWithIds(this.idOfCategoryOrgList);
            wtDiscountRule.getCategoryOrgs().addAll(categoryOrgList);
        }

        wtDiscountRule.setCategoryDiscounts(categoryDiscountSet);

        wtSelectedComplexes.clear();
        complexType = -1;
        ageGroup = -1;
        dietType = -1;
        contragentFilter = "Не выбрано";
        orgListFilter = "Не выбрано";

        daoService.persistEntity(wtDiscountRule);

        printMessage("Правило зарегистрировано успешно");
        onShow();
    }

    public int getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(int subCategory) {
        this.subCategory = subCategory;
    }

    public String getFilterOrg() {
        return filterOrg;
    }

    public void setFilterOrg(String filterOrg) {
        this.filterOrg = filterOrg;
    }

    public String getFilter() {
        return filter;
    }

    public Boolean getOperationOr() {
        return operationOr;
    }

    public void setOperationOr(Boolean operationOr) {
        this.operationOr = operationOr;
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

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
    }

    public List<Long> getIdOfCategoryList() {
        return idOfCategoryList;
    }

    public void setIdOfCategoryList(List<Long> idOfCategoryList) {
        this.idOfCategoryList = idOfCategoryList;
    }

    public List<Long> getIdOfCategoryOrgList() {
        return idOfCategoryOrgList;
    }

    public void setIdOfCategoryOrgList(List<Long> idOfCategoryOrgList) {
        this.idOfCategoryOrgList = idOfCategoryOrgList;
    }

    public String getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(String categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public DAOService getDaoService() {
        return daoService;
    }

    public void setDaoService(DAOService daoService) {
        this.daoService = daoService;
    }

    public long getComplexType() {
        return complexType;
    }

    public void setComplexType(long complexType) {
        this.complexType = complexType;
    }

    public long getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(long ageGroup) {
        this.ageGroup = ageGroup;
    }

    public long getDietType() {
        return dietType;
    }

    public void setDietType(long dietType) {
        this.dietType = dietType;
    }

    public List<WtSelectedComplex> getWtSelectedComplexes() {
        return wtSelectedComplexes;
    }

    public void setWtSelectedComplexes(List<WtSelectedComplex> wtSelectedComplexes) {
        this.wtSelectedComplexes = wtSelectedComplexes;
    }

    public String getContragentFilter() {
        return contragentFilter;
    }

    public void setContragentFilter(String contragentFilter) {
        this.contragentFilter = contragentFilter;
    }

    public String getContragentIds() {
        return contragentIds;
    }

    public void setContragentIds(String contragentIds) {
        this.contragentIds = contragentIds;
    }

    public List<ContragentItem> getContragentItems() {
        return contragentItems;
    }

    public void setContragentItems(List<ContragentItem> contragentItems) {
        this.contragentItems = contragentItems;
    }

    public String getOrgListFilter() {
        return orgListFilter;
    }

    public void setOrgListFilter(String orgListFilter) {
        this.orgListFilter = orgListFilter;
    }

    /// class ContragentItem ///
    public static class ContragentItem {

        private final Long idOfContragent;
        private final String contragentName;

        public ContragentItem(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contragentName = contragent.getContragentName();
        }

        public ContragentItem() {
            this.idOfContragent = null;
            this.contragentName = null;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }
}
