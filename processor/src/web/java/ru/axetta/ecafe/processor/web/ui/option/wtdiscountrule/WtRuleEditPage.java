/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WtRuleEditPage extends BasicWorkspacePage implements CategoryListSelectPage.CompleteHandlerList,
        CategoryOrgListSelectPage.CompleteHandlerList,
        ContragentListSelectPage.CompleteHandler {

    // Old
    private String description;
    private Integer discountRate = 100;
    private int priority;
    private boolean operationor;
    private String categoryDiscounts;
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private String filter = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;
    private Integer[] selectedComplexIds;
    private int subCategory;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @Autowired
    private DAOService daoService;
    DiscountRule entity;
    private String filterOrg = "Не выбрано";
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private Set<CategoryOrg> categoryOrgs;

    public String getPageFilename() {
        return "option/wtdiscountrule/edit";
    }

    // Веб-технолог
    private int complexType = -1;
    private int ageGroup = -1;
    private int supplier = -1;
    WtDiscountRule wtEntity;
    private List<WtSelectedComplex> wtSelectedComplexes = new ArrayList<>();
    private Map<Integer, Long> complexTypeMap;
    private Map<Integer, Long> ageGroupMap;
    private Map<Integer, Long> supplierMap;
    private boolean wt = false;
    private boolean showFilter = false;

    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private List<ContragentItem> contragentItems = new ArrayList<>();

    //// Веб-технолог ////

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
            if (item.getIdOfAgeGroupItem() < 5) { // 5 = Сотрудники, 6 = Все
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
        if (!wt || wtSelectedComplexes.size() != 0) {
            wtSelectedComplexes.clear();
        }
        if (!wt) {
            return;
        }

        if (complexType > -1 || ageGroup > -1 || !contragentItems.isEmpty()) {
            Long complexGroupId = complexTypeMap.get(complexType);
            Long ageGroupId = ageGroupMap.get(ageGroup);

            if (complexGroupId == null && ageGroupId == null && contragentItems.isEmpty()) {
                fill(wtEntity);
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
            fill(wtEntity);
        }
    }

    private void addWtComplex(WtComplexGroupItem complexGroupItem, WtAgeGroupItem ageGroupItem, Contragent contragent) {
        List<WtComplex> wtComplexes = new ArrayList<>();
        List<WtComplex> complexes = daoService.getWtComplexesList(complexGroupItem, ageGroupItem, contragent);
        wtComplexes.addAll(complexes);

        for (WtComplex wtComplex : wtComplexes) {
            if (wtComplex != null && wtSelectedComplexes != null) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex));
            }
        }
    }

    //private void resetWtForm() {
    //    List<WtSelectedComplex> wtNewSelectedComplexes = new ArrayList<>();
    //    List<WtComplex> wtComplexes = daoService.getWtComplexesList();
    //    for (WtComplex wtComplex : wtComplexes) {
    //        wtNewSelectedComplexes.add(new WtSelectedComplex(wtComplex));
    //    }
    //    wtSelectedComplexes = wtNewSelectedComplexes;
    //}

    private void fill(WtDiscountRule wtDiscountRule) {

        this.description = (wtDiscountRule.getDescription() == null) ? "" : wtDiscountRule.getDescription();

        //if (description.indexOf(CategoryDiscountEditPage.DISCOUNT_START) == 0) {
        //    String discount = description.substring(description.indexOf(CategoryDiscountEditPage.DISCOUNT_START)
        //                    + CategoryDiscountEditPage.DISCOUNT_START.length(),
        //            description.indexOf(CategoryDiscountEditPage.DISCOUNT_END));
        //    discountRate = Integer.parseInt(discount);
        //    description = "";
        //} else {
        //    discountRate = 100;
        //}

        discountRate = 100;
        subCategory = -1;

        for (int i = 0; i < WtRuleCreatePage.SUB_CATEGORIES.length; i++) {
            if (WtRuleCreatePage.SUB_CATEGORIES[i].equals(wtDiscountRule.getSubCategory())) {
                subCategory = i;
                break;
            }
        }

        this.priority = wtDiscountRule.getPriority();

        this.idOfCategoryList.clear();
        if (!wtDiscountRule.getCategoryDiscounts().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryDiscount categoryDiscount : wtDiscountRule.getCategoryDiscounts()) {
                stringBuilder.append(categoryDiscount.getCategoryName());
                stringBuilder.append("; ");
                this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
            }
            this.categoryDiscounts = stringBuilder.toString();
        }
        this.idOfCategoryOrgList.clear();
        if (!wtDiscountRule.getCategoryOrgs().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (CategoryOrg categoryOrg : wtDiscountRule.getCategoryOrgs()) {
                stringBuilder.append(categoryOrg.getCategoryName());
                stringBuilder.append("; ");
                this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
            }
        }
        this.operationor = wtDiscountRule.isOperationOr();
        for (WtComplex wtComplex : wtDiscountRule.getComplexes()) {
            if (wtComplex != null && wtSelectedComplexes != null) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex));
            }
        }

        this.wt = true;
    }

    public List<SelectItem> getSubCategories() throws Exception {
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (int i = 0; i < WtRuleCreatePage.SUB_CATEGORIES.length; i++) {
            String group = WtRuleCreatePage.SUB_CATEGORIES[i];
            res.add(new SelectItem(i, group));
        }
        return res;
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

    @Transactional
    public void updateRule() throws Exception {
        if (wt) {
            //// Веб-технолог ////
            wtEntity = em.merge(wtEntity);

            String strSubCategory = "";
            if (this.subCategory > 0) {
                strSubCategory = WtRuleCreatePage.SUB_CATEGORIES[this.subCategory];
            }
            wtEntity.setSubCategory(strSubCategory);

            wtEntity.setPriority(priority);
            wtEntity.setOperationOr(operationor);

            this.categoryDiscountSet = new HashSet<CategoryDiscount>();
            wtEntity.getCategoryDiscounts().clear();

            List categoryList = DAOUtils.getCategoryDiscountListWithIds(em, this.idOfCategoryList);
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object : categoryList) {
                this.categoryDiscountSet.add((CategoryDiscount) object);
                stringBuilder.append(((CategoryDiscount) object).getIdOfCategoryDiscount());
                stringBuilder.append(",");
            }
            wtEntity.setCategoryDiscounts(this.categoryDiscountSet);

            wtEntity.getCategoryOrgs().clear();
            if (!this.idOfCategoryOrgList.isEmpty()) {
                wtEntity.getCategoryOrgs().clear();
                List<CategoryOrg> categoryOrgList = DAOUtils.getCategoryOrgWithIds(em, this.idOfCategoryOrgList);
                for (Object object : categoryOrgList) {
                    wtEntity.getCategoryOrgs().add((CategoryOrg) object);
                }
            }

            for (WtSelectedComplex wtSelectedComplex : wtSelectedComplexes) {
                if (wtSelectedComplex.isChecked()) {
                    WtComplex complex = wtSelectedComplex.getWtComplex();
                    wtEntity.getComplexes().add(complex);
                }
            }

            wtEntity.setDescription(description);
            wtEntity.setPriority(priority);
            wtEntity.setRate(discountRate);
            wtEntity.setOperationOr(operationor);

            wtEntity.setCategoryDiscounts(this.categoryDiscountSet);

            em.persist(wtEntity);
            fill(wtEntity);
        }

        printMessage("Данные обновлены");
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public void reload() throws Exception {

        //wt
        if (wtEntity != null) {
            WtDiscountRule wtEntity = em.merge(this.wtEntity);

            StringBuilder categoryFilter = new StringBuilder();
            if (!this.wtEntity.getCategoryDiscounts().isEmpty()) {
                for (CategoryDiscount categoryDiscount : this.wtEntity.getCategoryDiscounts()) {
                    this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
                    categoryFilter.append(categoryDiscount.getCategoryName());
                    categoryFilter.append(";");
                }
                this.filter = categoryFilter.substring(0, categoryFilter.length() - 1);
            } else {
                this.filter = "Не выбрано";
            }

            StringBuilder categoryOrgFilter = new StringBuilder();
            if (!this.wtEntity.getCategoryOrgs().isEmpty()) {
                for (CategoryOrg categoryOrg : this.wtEntity.getCategoryOrgs()) {
                    this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
                    categoryOrgFilter.append(categoryOrg.getCategoryName());
                    categoryOrgFilter.append("; ");
                }
                this.filterOrg = categoryOrgFilter.substring(0, categoryOrgFilter.length() - 1);
            } else {
                this.filterOrg = "Не выбрано";
            }

            fill(this.wtEntity);
        }
    }

    public Integer[] getSelectedComplexIds() {
        return selectedComplexIds;
    }

    public void setSelectedComplexIds(Integer[] selectedComplexIds) {
        this.selectedComplexIds = selectedComplexIds;
    }

    public String getIdOfCategoryOrgListString() {
        return idOfCategoryOrgList.toString().replaceAll("[^(0-9-),]", "");
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]", "");
    }

    public Integer getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(Integer discountRate) {
        this.discountRate = discountRate;
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

    public DiscountRule getEntity() {
        return entity;
    }

    public void setEntity(DiscountRule entity) {
        this.entity = entity;
    }

    public String getEntityName() {
        return wtEntity.getDescription();
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

    public WtDiscountRule getWtEntity() {
        return wtEntity;
    }

    public void setWtEntity(WtDiscountRule wtDiscountRule) {
        this.wtEntity = wtDiscountRule;
    }

    public List<WtSelectedComplex> getWtSelectedComplexes() {
        return wtSelectedComplexes;
    }

    public void setWtSelectedComplexes(List<WtSelectedComplex> wtSelectedComplexes) {
        this.wtSelectedComplexes = wtSelectedComplexes;
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