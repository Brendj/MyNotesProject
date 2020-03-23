/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.ComplexRole;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

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
        CategoryOrgListSelectPage.CompleteHandlerList,
        ContragentListSelectPage.CompleteHandler {

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
    private Integer discountRate = 100;
    private Integer[] selectedComplexIds;
    private int subCategory = -1;
    @Autowired
    private DAOService daoService;

    // Веб-технолог
    private int complexType = -1;
    private int ageGroup = -1;
    private int supplier = -1;
    WtDiscountRule wtEntity;
    private List<WtSelectedComplex> wtSelectedComplexes = new ArrayList<>();
    private Map<Integer, Long> complexTypeMap;
    private Map<Integer, Long> ageGroupMap;
    private Map<Integer, Long> supplierMap;
    private boolean wt = true;
    private boolean showFilter = true;

    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private List<ContragentItem> contragentItems = new ArrayList<>();

    public List<SelectItem> getAvailableComplexs() {
        final List<ComplexRole> complexRoles = daoService.findComplexRoles();
        final int size = complexRoles.size();
        List<SelectItem> list = new ArrayList<SelectItem>(size);
        for (int i = 0; i < size; i++) {
            ComplexRole complexRole = complexRoles.get(i);
            String complexName = String.format("Комплекс %d", i);
            if (!complexName.equals(complexRole.getRoleName())) {
                complexName = String.format("Комплекс %d - %s", i, complexRole.getRoleName());
            }
            SelectItem selectItem = new SelectItem(i, complexName);
            list.add(selectItem);
        }
        return list;
    }

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
        complexTypeMap = new HashMap<>();
        res.add(new SelectItem(0, " "));
        complexTypeMap.put(0, 0L);
        complexGroupItems = daoService.getWtComplexGroupList();
        int i = 0;
        for (WtComplexGroupItem item : complexGroupItems) {
            if (item.getIdOfComplexGroupItem() != 3) { // 3 = Все виды питания
                res.add(new SelectItem(++i, item.getDescription()));
                complexTypeMap.put(i, item.getIdOfComplexGroupItem());
            }
        }
        return res;
    }

    public List<SelectItem> getAgeGroups() {
        List<SelectItem> res = new ArrayList<>();
        List<WtAgeGroupItem> ageGroupItems;
        ageGroupMap = new HashMap<>();
        res.add(new SelectItem(0, " "));
        ageGroupItems = daoService.getWtAgeGroupList();
        int i = 0;
        for (WtAgeGroupItem item : ageGroupItems) {
            if (item.getIdOfAgeGroupItem() != 5 || item.getIdOfAgeGroupItem() != 6) { // 5 = Сотрудники, 6 = Все
                res.add(new SelectItem(++i, item.getDescription()));
                ageGroupMap.put(i, item.getIdOfAgeGroupItem());
            }
        }
        return res;
    }

    public List<SelectItem> getSuppliers() {
        List<SelectItem> res = new ArrayList<>();
        List<Contragent> suppliers;
        supplierMap = new HashMap<>();
        res.add(new SelectItem(0, " "));
        suppliers = daoService.getSupplierList();
        int i = 0;
        for (Contragent item : suppliers) {
            res.add(new SelectItem(++i, item.getContragentName()));
            supplierMap.put(i, item.getIdOfContragent());
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

    public void fillWtSelectedComplexes() {
        if (!wt) {
            wtSelectedComplexes.clear();
            return;
        }

        if (complexType > -1 || ageGroup > -1 || !contragentItems.isEmpty()) {
            Long complexGroupId = complexTypeMap.get(complexType);
            Long ageGroupId = ageGroupMap.get(ageGroup);

            if (complexGroupId == null && ageGroupId == null && contragentItems.isEmpty()) {
                return;
            } else {
                List<WtComplexGroupItem> wtComplexGroupItem = null;
                List<WtAgeGroupItem> wtAgeGroupItem = null;
                List<Contragent> contragents = new ArrayList<>();

                if (!contragentItems.isEmpty()) {
                    for (ContragentItem contragentItem : contragentItems) {
                        try {
                            Contragent contragent = daoService.getContragentById(contragentItem.idOfContragent);
                            if (contragent != null) {
                                contragents.add(contragent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (complexGroupId != null) {
                    wtComplexGroupItem = daoService.getWtComplexGroupItemById(complexGroupId);
                }
                if (ageGroupId != null) {
                    wtAgeGroupItem = daoService.getWtAgeGroupItemById(ageGroupId);
                }

                if (wtComplexGroupItem != null && wtAgeGroupItem != null && !contragents.isEmpty()) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                            for (Contragent contragent : contragents) {
                                addWtComplex(complexGroupItem, ageGroupItem, contragent);
                            }
                        }
                    }
                } else if (wtComplexGroupItem == null && wtAgeGroupItem != null && !contragents.isEmpty()) {
                    for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                        for (Contragent contragent : contragents) {
                            addWtComplex(null, ageGroupItem, contragent);
                        }
                    }
                } else if (wtComplexGroupItem != null && wtAgeGroupItem == null && !contragents.isEmpty()) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        for (Contragent contragent : contragents) {
                            addWtComplex(complexGroupItem, null, contragent);
                        }
                    }
                } else if (wtComplexGroupItem != null && wtAgeGroupItem != null && contragents.isEmpty()) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                            addWtComplex(complexGroupItem, ageGroupItem, null);
                        }
                    }
                } else if (wtComplexGroupItem == null && wtAgeGroupItem == null && !contragents.isEmpty()) {
                    for (Contragent contragent : contragents) {
                        addWtComplex(null, null, contragent);
                    }
                } else if (wtComplexGroupItem != null && wtAgeGroupItem == null && contragents.isEmpty()) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        addWtComplex(complexGroupItem, null, null);
                    }
                } else if (wtComplexGroupItem == null && wtAgeGroupItem != null && contragents.isEmpty()) {
                    for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                        addWtComplex(null, ageGroupItem, null);
                    }
                }
            }
        } else if (complexType == -1 && ageGroup == -1 && contragentItems.isEmpty()) {
            return;
        }
    }

    private void addWtComplex(WtComplexGroupItem complexGroupItem, WtAgeGroupItem ageGroupItem, Contragent contragent) {
        List<WtComplex> complexes = daoService.getWtComplexesList(complexGroupItem, ageGroupItem, contragent, null);
        List<WtComplex> wtComplexes = new ArrayList<>(complexes);
        for (WtComplex wtComplex : wtComplexes) {
            if (wtComplex != null && wtSelectedComplexes != null) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex));
            }
        }
    }

    @Override
    public void onShow() throws Exception {
        this.description = "";
        this.priority = 0;
        this.categoryDiscounts = "";
        this.operationOr = false;
        this.filter = "Не выбрано";
        this.filterOrg = "Не выбрано";
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

        for (WtSelectedComplex wtSelectedComplex : wtSelectedComplexes) {
            if (wtSelectedComplex.isChecked()) {
                WtComplex complex = wtSelectedComplex.getWtComplex();
                wtDiscountRule.getComplexes().add(complex);
            }
        }

        wtDiscountRule.setOperationOr(operationOr);
        wtDiscountRule.setPriority(priority);
        wtDiscountRule.setPriority(discountRate);

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

        daoService.persistEntity(wtDiscountRule);

        printMessage("Правило зарегистрировано успешно");
    }

    public Integer[] getSelectedComplexIds() {
        return selectedComplexIds;
    }

    public void setSelectedComplexIds(Integer[] selectedComplexIds) {
        this.selectedComplexIds = selectedComplexIds;
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

    public int getComplexType() {
        return complexType;
    }

    public void setComplexType(int complexType) {
        this.complexType = complexType;
    }

    public int getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(int ageGroup) {
        this.ageGroup = ageGroup;
    }

    public WtDiscountRule getWtEntity() {
        return wtEntity;
    }

    public void setWtEntity(WtDiscountRule wtEntity) {
        this.wtEntity = wtEntity;
    }

    public List<WtSelectedComplex> getWtSelectedComplexes() {
        return wtSelectedComplexes;
    }

    public void setWtSelectedComplexes(List<WtSelectedComplex> wtSelectedComplexes) {
        this.wtSelectedComplexes = wtSelectedComplexes;
    }

    public Map<Integer, Long> getComplexTypeMap() {
        return complexTypeMap;
    }

    public void setComplexTypeMap(Map<Integer, Long> complexTypeMap) {
        this.complexTypeMap = complexTypeMap;
    }

    public Map<Integer, Long> getAgeGroupMap() {
        return ageGroupMap;
    }

    public void setAgeGroupMap(Map<Integer, Long> ageGroupMap) {
        this.ageGroupMap = ageGroupMap;
    }

    public boolean isWt() {
        return wt;
    }

    public void setWt(boolean wt) {
        this.wt = wt;
        this.showFilter = wt;
    }

    public boolean isShowFilter() {
        return showFilter;
    }

    public void setShowFilter(boolean showFilter) {
        this.showFilter = showFilter;
    }

    public int getSupplier() {
        return supplier;
    }

    public void setSupplier(int supplier) {
        this.supplier = supplier;
    }

    public Map<Integer, Long> getSupplierMap() {
        return supplierMap;
    }

    public void setSupplierMap(Map<Integer, Long> supplierMap) {
        this.supplierMap = supplierMap;
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
