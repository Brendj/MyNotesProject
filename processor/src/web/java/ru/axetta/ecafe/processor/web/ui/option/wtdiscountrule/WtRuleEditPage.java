/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.wtdiscountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.persistence.CategoryOrg;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountEditPage;
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

    private String description;
    private Integer discountRate;
    private int priority;
    private boolean operationor;
    private String categoryDiscounts;
    private List<Long> idOfCategoryList = new ArrayList<Long>();
    private String filter = "Не выбрано";
    private Set<CategoryDiscount> categoryDiscountSet;
    private int subCategory;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @Autowired
    private DAOService daoService;
    private String filterOrg = "Не выбрано";
    private List<Long> idOfCategoryOrgList = new ArrayList<Long>();
    private Set<CategoryOrg> categoryOrgs;

    public String getPageFilename() {
        return "option/wtdiscountrule/edit";
    }

    private long complexType = -1L;
    private long ageGroup = -1L;
    private long dietType = -1L;

    WtDiscountRule wtEntity;
    private List<WtSelectedComplex> wtSelectedComplexes = new ArrayList<>();

    private String contragentFilter = "Не выбрано";
    private String contragentIds;
    private List<ContragentItem> contragentItems = new ArrayList<>();

    private boolean applyFilter = false;

    public List<SelectItem> getComplexTypes() {
        List<SelectItem> res = new ArrayList<>();
        List<WtComplexGroupItem> complexGroupItems;
        res.add(new SelectItem(0, " "));
        complexGroupItems = daoService.getWtComplexGroupList();
        Long valueAllId = daoService.getWtComplexGroupValueAll();
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
        Long valueAllId = daoService.getWtAgeGroupValueAll();
        for (WtAgeGroupItem item : ageGroupItems) {
            if (valueAllId > 0 && !item.getIdOfAgeGroupItem().equals(valueAllId)) {
                res.add(new SelectItem(item.getIdOfAgeGroupItem(), item.getDescription()));
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

    public void fillWtSelectedComplexes() {
        wtSelectedComplexes.clear();
        if (isComplexFilterEmpty()) {
            List<WtComplex> ruleComplexes = DAOUtils.getComplexesByWtDiscountRule(em, wtEntity);
            wtSelectedComplexes.addAll(getCheckedComplexes(ruleComplexes));
            List<WtComplex> wtComplexList = daoService.getWtComplexesList();
            addUniqueUncheckedComplexes(wtSelectedComplexes, wtComplexList);
        } else {
            List<Long> wtComplexGroupIds = new ArrayList<>();
            List<Long> wtAgeGroupIds = new ArrayList<>();
            List<Long> contragentIdList = new ArrayList<>();
            long dietTypeId = 0L;

            if (!contragentItems.isEmpty()) {
                for (ContragentItem contragentItem : contragentItems) {
                    contragentIdList.add(contragentItem.idOfContragent);
                }
            }

            if (complexType > 0) {
                wtComplexGroupIds.add(complexType);
                Long valueAllId = daoService.getWtComplexGroupValueAll();
                if (valueAllId > 0) {
                    wtComplexGroupIds.add(valueAllId);
                }
            }
            if (ageGroup > 0) {
                wtAgeGroupIds.add(ageGroup);
                Long valueAllId = daoService.getWtAgeGroupValueAll();
                if (valueAllId > 0) {
                    wtAgeGroupIds.add(valueAllId);
                }
            }
            if (dietType > 0) {
                dietTypeId = dietType;
            }

            List<WtComplex> wtComplexes = daoService.getWtComplexesListByFilter(wtComplexGroupIds, wtAgeGroupIds,
                    dietTypeId, contragentIdList);

            List<WtComplex> ruleComplexList = new ArrayList<>();
            if (wtEntity != null) {
                if (applyFilter) {
                    ruleComplexList = daoService.getWtComplexesListByDiscountRuleAndFilter(wtComplexGroupIds, wtAgeGroupIds,
                            dietTypeId, contragentIdList, wtEntity);
                } else {
                    ruleComplexList = DAOUtils.getComplexesByWtDiscountRule(em, wtEntity);
                }
            }

            wtSelectedComplexes.addAll(getCheckedComplexes(ruleComplexList));
            addUniqueUncheckedComplexes(wtSelectedComplexes, wtComplexes);
        }
    }

    private boolean isComplexFilterEmpty() {
        return complexType == 0 && ageGroup == 0 && dietType == 0 && contragentItems.isEmpty();
    }

    private void addUniqueUncheckedComplexes(List<WtSelectedComplex> wtSelectedComplexes, List<WtComplex> wtComplexes) {
        for (WtComplex wtComplex : wtComplexes) {
            WtSelectedComplex fakeCheckedComplex = new WtSelectedComplex(wtComplex, true);
            if (!wtSelectedComplexes.contains(fakeCheckedComplex)) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex, false));
            }
        }
    }

    private void fill(WtDiscountRule wtDiscountRule) {
        this.description = wtDiscountRule.getDescription();

        if (description.indexOf(CategoryDiscountEditPage.DISCOUNT_START) == 0) {
            String discount = description.substring(description.indexOf(CategoryDiscountEditPage.DISCOUNT_START)
                            + CategoryDiscountEditPage.DISCOUNT_START.length(),
                    description.indexOf(CategoryDiscountEditPage.DISCOUNT_END));
            discountRate = Integer.parseInt(discount);
            description = "";
        } else {
            discountRate = wtDiscountRule.getRate();
        }

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

        wtSelectedComplexes.clear();
        List<WtComplex> ruleComplexes = DAOUtils.getComplexesByWtDiscountRule(em, wtEntity);
        wtSelectedComplexes.addAll(getCheckedComplexes(ruleComplexes));

        complexType = -1;
        ageGroup = -1;
        dietType = -1;
        contragentFilter = "Не выбрано";
        applyFilter = false;
    }

    private List<WtSelectedComplex> getCheckedComplexes(List<WtComplex> complexes) {
        List<WtSelectedComplex> resultComplexes = new ArrayList<>();
        if (!complexes.isEmpty()) {
            for (WtComplex wtComplex : complexes) {
                WtSelectedComplex wtSelectedComplex = new WtSelectedComplex(wtComplex, true);
                resultComplexes.add(wtSelectedComplex);
            }
        }
        return resultComplexes;
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
        if (wtEntity != null) {
            wtEntity = em.merge(wtEntity);

            if (discountRate != null && discountRate != 100) {
                description =
                        CategoryDiscountEditPage.DISCOUNT_START + discountRate + CategoryDiscountEditPage.DISCOUNT_END;
            }

            wtEntity.setDescription(description);

            String strSubCategory = "";
            if (this.subCategory > 0) {
                strSubCategory = WtRuleCreatePage.SUB_CATEGORIES[this.subCategory];
            }
            wtEntity.setSubCategory(strSubCategory);

            wtEntity.setPriority(priority);
            wtEntity.setRate(discountRate);
            wtEntity.setOperationOr(operationor);

            this.categoryDiscountSet = new HashSet<CategoryDiscount>();
            wtEntity.getCategoryDiscounts().clear();

            if (!this.idOfCategoryList.isEmpty()) {
                List categoryList = DAOUtils.getCategoryDiscountListWithIds(em, this.idOfCategoryList);
                StringBuilder stringBuilder = new StringBuilder();
                for (Object object : categoryList) {
                    this.categoryDiscountSet.add((CategoryDiscount) object);
                    stringBuilder.append(((CategoryDiscount) object).getIdOfCategoryDiscount());
                    stringBuilder.append(",");
                }
                wtEntity.setCategoryDiscounts(this.categoryDiscountSet);
            }

            wtEntity.getCategoryOrgs().clear();
            if (!this.idOfCategoryOrgList.isEmpty()) {
                wtEntity.getCategoryOrgs().clear();
                List<CategoryOrg> categoryOrgList = DAOUtils.getCategoryOrgWithIds(em, this.idOfCategoryOrgList);
                for (Object object : categoryOrgList) {
                    wtEntity.getCategoryOrgs().add((CategoryOrg) object);
                }
            }

            Set<WtComplex> newComplexes = new HashSet<>();
            for (WtSelectedComplex wtSelectedComplex : wtSelectedComplexes) {
                if (wtSelectedComplex.isChecked()) {
                    newComplexes.add(wtSelectedComplex.getWtComplex());
                }
            }
            wtEntity.setComplexes(newComplexes);

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
        if (this.wtEntity != null) {

            WtDiscountRule wtEntity = em.merge(this.wtEntity);

            StringBuilder categoryFilter = new StringBuilder();
            if (!wtEntity.getCategoryDiscounts().isEmpty()) {
                for (CategoryDiscount categoryDiscount : wtEntity.getCategoryDiscounts()) {
                    this.idOfCategoryList.add(categoryDiscount.getIdOfCategoryDiscount());
                    categoryFilter.append(categoryDiscount.getCategoryName());
                    categoryFilter.append(";");
                }
                this.filter = categoryFilter.substring(0, categoryFilter.length() - 1);
            } else {
                this.filter = "Не выбрано";
            }

            StringBuilder categoryOrgFilter = new StringBuilder();
            if (!wtEntity.getCategoryOrgs().isEmpty()) {
                for (CategoryOrg categoryOrg : wtEntity.getCategoryOrgs()) {
                    this.idOfCategoryOrgList.add(categoryOrg.getIdOfCategoryOrg());
                    categoryOrgFilter.append(categoryOrg.getCategoryName());
                    categoryOrgFilter.append("; ");
                }
                this.filterOrg = categoryOrgFilter.substring(0, categoryOrgFilter.length() - 1);
            } else {
                this.filterOrg = "Не выбрано";
            }
            fill(wtEntity);
        }
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

    public String getEntityName() {
        return wtEntity.getDescription();
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

    public boolean isApplyFilter() {
        return applyFilter;
    }

    public void setApplyFilter(boolean applyFilter) {
        this.applyFilter = applyFilter;
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

        public ContragentItem(Long idOfContragent, String contragentName) {
            this.idOfContragent = idOfContragent;
            this.contragentName = contragentName;
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public String getContragentName() {
            return contragentName;
        }
    }
}