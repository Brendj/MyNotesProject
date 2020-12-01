/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountEditPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

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
    private Integer codeMSP;
    private List<SelectItem> allMSP = loadAllMSP();

    @Autowired
    private DAOService daoService;

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

    private List<SelectItem> loadAllMSP() {
        List<CodeMSP> items = Collections.emptyList();
        List<SelectItem> result = new LinkedList<>();

        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            items = DAOUtils.getAllCodeMSP(session);
            session.close();

            result.add(new SelectItem(null, ""));
            for(CodeMSP code : items){
                SelectItem selectItem = new SelectItem(code.getCode(), code.getCode().toString());
                result.add(selectItem);
            }

            return result;
        } finally {
            HibernateUtils.close(session, getLogger());
        }
    }

    public List<SelectItem> getAllMSP() {
        return allMSP;
    }

    public void setAllMSP(List<SelectItem> allMSP) {
        this.allMSP = allMSP;
    }

    public Integer getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(Integer codeMSP) {
        this.codeMSP = codeMSP;
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
        DiscountRule discountRule = new DiscountRule();
        if (discountRate != null && discountRate != 100) {
            description = CategoryDiscountEditPage.DISCOUNT_START + discountRate +
                    CategoryDiscountEditPage.DISCOUNT_END;
        }
        String subCategory = "";
        if(this.subCategory > 0) {
            subCategory = SUB_CATEGORIES [this.subCategory];
        }
        discountRule.setSubCategory(subCategory);
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
        discountRule.setDeletedState(false);
        discountRule.setCategoryDiscounts(categoryDiscounts);
        discountRule.setCodeMSP(DAOService.getInstance().findCodeNSPByCode(codeMSP));
        Set<CategoryDiscount> categoryDiscountSet = new HashSet<CategoryDiscount>();
        if (!this.idOfCategoryList.isEmpty()) {
            List<CategoryDiscount> categoryList = daoService.getCategoryDiscountListWithIds(this.idOfCategoryList);
            categoryDiscountSet.addAll(categoryList);
            discountRule.setCategoriesDiscounts(categoryDiscountSet);
        }
        if (!this.idOfCategoryOrgList.isEmpty()) {
            List<CategoryOrg> categoryOrgList = daoService.getCategoryOrgWithIds(this.idOfCategoryOrgList);
            discountRule.getCategoryOrgs().addAll(categoryOrgList);
        }
        daoService.persistEntity(discountRule);
        //em.persist(discountRule);
        printMessage("Правило зарегистрировано успешно");
    }

    public Integer[] getSelectedComplexIds() {
        return selectedComplexIds;
    }

    public void setSelectedComplexIds(Integer[] selectedComplexIds) {
        this.selectedComplexIds = selectedComplexIds;
    }
}
