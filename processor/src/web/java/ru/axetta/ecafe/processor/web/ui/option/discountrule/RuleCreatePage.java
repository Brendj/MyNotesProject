/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.*;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountEditPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
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
    public static final String SUB_CATEGORIES [] = new String []
            { "",
              "Обучающиеся из многодетных семей 5-9 кл. (завтрак+обед)",
              "Обучающиеся из многодетных семей 10-11 кл. (завтрак+обед)",
              "Обучающиеся 5-9 кл.(завтрак+обед)",
              "Обучающиеся 10-11 кл.(завтрак+обед)",
              "Обучающиеся 1-4 кл. (завтрак)",
              "Обучающиеся из соц. незащищ. семей 1-4 кл. (завтрак+обед)",            //  Если измениться, необходимо поменять DailyReferReort.getReportData : 353
              "Обучающиеся из соц. незащищ. семей 5-9 кл. (завтрак+обед)",            //  Если измениться, необходимо поменять DailyReferReort.getReportData : 354
              "Обучающиеся из соц. незащищ. семей 10-11 кл. (завтрак+обед)",          //  Если измениться, необходимо поменять DailyReferReort.getReportData : 354
              "Обучающиеся из многодетных семей 1-4 кл. (завтрак+обед)",
              "Обучающиеся 1-4 кл.(завтрак+обед)",

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
    WtDiscountRule wtDiscountRule = new WtDiscountRule();
    private List<WtSelectedComplex> wtSelectedComplexes = new ArrayList<>();
    private Map<Integer, Long> complexTypeMap;
    private Map<Integer, Long> ageGroupMap;
    private Map<Integer, Long> supplierMap;
    boolean wt = false;
    boolean showFilter = false;

    public List<SelectItem> getAvailableComplexs() {
        final List<ComplexRole> complexRoles = daoService.findComplexRoles();
        final int size = complexRoles.size();
        List<SelectItem> list = new ArrayList<SelectItem>(size);
        for (int i=0;i<size;i++) {
            ComplexRole complexRole = complexRoles.get(i);
            String complexName = String.format("Комплекс %d", i);
            if(!complexName.equals(complexRole.getRoleName())){
                complexName = String.format("Комплекс %d - %s", i, complexRole.getRoleName());
            }
            SelectItem selectItem = new SelectItem(i,complexName);
            list.add(selectItem);
        }
        return list;
    }

    public List<SelectItem> getSubCategories() throws Exception {
        List<SelectItem> res = new ArrayList<SelectItem>();
        res.add(new SelectItem("", ""));
        for (int i=0; i<SUB_CATEGORIES.length; i++) {
            String group = SUB_CATEGORIES[i];
            res.add(new SelectItem(i, group));
        }
        return res;
    }

    public void completeCategoryListSelection(Map<Long, String> categoryMap) throws HibernateException {
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

    public void fillWtSelectedComplexes() {
        if (!wt || wtSelectedComplexes.size() != 0) {
            wtSelectedComplexes.clear();
        }
        if (!wt) {
            return;
        }
        if (complexType > -1 || ageGroup > -1 || supplier > -1) {
            Long complexGroupId = complexTypeMap.get(complexType);
            Long ageGroupId = ageGroupMap.get(ageGroup);
            Long supplierId = supplierMap.get(supplier);

            if (complexGroupId == null && ageGroupId == null && supplierId == null) {
                fill(wtDiscountRule);
            } else {
                List<WtComplexGroupItem> wtComplexGroupItem = null;
                List<WtAgeGroupItem> wtAgeGroupItem = null;
                List<Contragent> supplierItem = null;
                if (complexGroupId != null) {
                    wtComplexGroupItem = daoService.getWtComplexGroupItemById(complexGroupId);
                }
                if (ageGroupId != null) {
                    wtAgeGroupItem = daoService.getWtAgeGroupItemById(ageGroupId);
                }
                if (supplierId != null) {
                    supplierItem = daoService.getSupplierItemById(supplierId);
                }
                if (wtComplexGroupItem != null && wtAgeGroupItem != null && supplierItem != null) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                            for (Contragent supplier : supplierItem) {
                                addWtComplex(complexGroupItem, ageGroupItem, supplier);
                            }
                        }
                    }
                } else if (wtComplexGroupItem == null && wtAgeGroupItem != null && supplierItem != null) {
                    for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                        for (Contragent supplier : supplierItem) {
                            addWtComplex(null, ageGroupItem, supplier);
                        }
                    }
                } else if (wtComplexGroupItem != null && wtAgeGroupItem == null && supplierItem != null) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        for (Contragent supplier : supplierItem) {
                            addWtComplex(complexGroupItem, null, supplier);
                        }
                    }
                } else if (wtComplexGroupItem != null && wtAgeGroupItem != null && supplierItem == null) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                            addWtComplex(complexGroupItem, ageGroupItem, null);
                        }
                    }
                } else if (wtComplexGroupItem == null && wtAgeGroupItem == null && supplierItem != null) {
                    for (Contragent supplier : supplierItem) {
                        addWtComplex(null, null, supplier);
                    }
                } else if (wtComplexGroupItem != null && wtAgeGroupItem == null && supplierItem == null) {
                    for (WtComplexGroupItem complexGroupItem : wtComplexGroupItem) {
                        addWtComplex(complexGroupItem, null, null);
                    }
                } else if (wtComplexGroupItem == null && wtAgeGroupItem != null && supplierItem == null) {
                    for (WtAgeGroupItem ageGroupItem : wtAgeGroupItem) {
                        addWtComplex(null, ageGroupItem, null);
                    }
                }
            }
        } else if (complexType == -1 && ageGroup == -1 && supplier == -1) {
            fill(wtDiscountRule);
        }
    }

    private void addWtComplex(WtComplexGroupItem complexGroupItem, WtAgeGroupItem ageGroupItem, Contragent supplier) {
        List<WtComplex> wtComplexes = daoService.getWtComplexesList(complexGroupItem, ageGroupItem, supplier);
        for (WtComplex wtComplex : wtComplexes) {
            if (wtComplex != null && wtSelectedComplexes != null) {
                wtSelectedComplexes.add(new WtSelectedComplex(wtComplex));
            }
        }
    }

    private void fill(WtDiscountRule wtDiscountRule) {

        if (wtDiscountRule != null) {
            this.description = wtDiscountRule.getDescription();
        }

        discountRate = 100;
        subCategory = -1;
        for (int i = 0; i < RuleCreatePage.SUB_CATEGORIES.length; i++) {
            if (RuleCreatePage.SUB_CATEGORIES[i].equals(wtDiscountRule.getSubCategory())) {
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

    @Transactional
    public void createRule() throws Exception {

        if (discountRate != null && discountRate != 100) {
            description = CategoryDiscountEditPage.DISCOUNT_START + discountRate + CategoryDiscountEditPage.DISCOUNT_END;
        }

        String strSubCategory = "";
        if (this.subCategory > 0) {
            strSubCategory = SUB_CATEGORIES[this.subCategory];
        }

        Set<CategoryDiscount> categoryDiscountSet = new HashSet<>();
        List<CategoryOrg> categoryOrgList = new ArrayList<>();

        if (!this.idOfCategoryList.isEmpty()) {
            List<CategoryDiscount> categoryList = daoService.getCategoryDiscountListWithIds(this.idOfCategoryList);
            categoryDiscountSet.addAll(categoryList);
        }

        if (!this.idOfCategoryOrgList.isEmpty()) {
            categoryOrgList = daoService.getCategoryOrgWithIds(this.idOfCategoryOrgList);
        }

        if (!wt) {
            DiscountRule discountRule = new DiscountRule();

            discountRule.setSubCategory(strSubCategory);
            discountRule.setDescription(description);

            List<Integer> selectedComplex = Arrays.asList(selectedComplexIds);
            discountRule.setComplex0(selectedComplex.contains(0) ? 1 : 0);
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

            if (!this.idOfCategoryList.isEmpty()) {
                discountRule.setCategoriesDiscounts(categoryDiscountSet);
            }

            if (!this.idOfCategoryOrgList.isEmpty()) {
                discountRule.getCategoryOrgs().addAll(categoryOrgList);
            }

            daoService.persistEntity(discountRule);
            //em.persist(discountRule);

        } else {

            //// Веб-технолог ////
            if (StringUtils.isNotEmpty(strSubCategory)) {
                wtDiscountRule.setSubCategory(strSubCategory);
            }

            for (WtSelectedComplex wtSelectedComplex : wtSelectedComplexes) {
                if (wtSelectedComplex.isChecked()) {
                    WtComplex complex = wtSelectedComplex.getWtComplex();
                    wtDiscountRule.getComplexes().add(complex);
                }
            }

            wtDiscountRule.setOperationOr(operationOr);
            wtDiscountRule.setDescription(description);
            wtDiscountRule.setPriority(priority);
            wtDiscountRule.setPriority(discountRate);
            wtDiscountRule.setCategoryDiscounts(categoryDiscountSet);

            if (!this.idOfCategoryList.isEmpty()) {
                wtDiscountRule.setCategoryDiscounts(categoryDiscountSet);
            }

            if (!this.idOfCategoryOrgList.isEmpty()) {
                wtDiscountRule.getCategoryOrgs().addAll(categoryOrgList);
            }

            //for (CategoryOrg categoryOrg : categoryOrgList) {
            //    wtDiscountRule.getCategoryOrgs().add(categoryOrg);
            //}

            daoService.persistEntity(wtDiscountRule);
        }

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

    public WtDiscountRule getWtDiscountRule() {
        return wtDiscountRule;
    }

    public void setWtDiscountRule(WtDiscountRule wtDiscountRule) {
        this.wtDiscountRule = wtDiscountRule;
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
}
