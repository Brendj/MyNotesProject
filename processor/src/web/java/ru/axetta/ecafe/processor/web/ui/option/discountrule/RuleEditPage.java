/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.discountrule;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryDiscountEditPage;
import ru.axetta.ecafe.processor.web.ui.option.categorydiscount.CategoryListSelectPage;
import ru.axetta.ecafe.processor.web.ui.option.categoryorg.CategoryOrgListSelectPage;

import org.apache.commons.lang.StringUtils;
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
public class RuleEditPage extends BasicWorkspacePage implements CategoryListSelectPage.CompleteHandlerList, CategoryOrgListSelectPage.CompleteHandlerList{

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
    private Integer codeMSP;
    private List<SelectItem> allMSP = loadAllMSP();

    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;
    @Autowired
    private DAOService daoService;

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

    public Integer[] getSelectedComplexIds() {
        return selectedComplexIds;
    }

    public void setSelectedComplexIds(Integer[] selectedComplexIds) {
        this.selectedComplexIds = selectedComplexIds;
    }

    public List<SelectItem> getAvailableComplexs() {
        final List<ComplexRole> complexRoles = DAOReadonlyService.getInstance().findComplexRoles();
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

    public Integer getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(Integer codeMSP) {
        this.codeMSP = codeMSP;
    }

    public String getIdOfCategoryOrgListString() {
        return idOfCategoryOrgList.toString().replaceAll("[^(0-9-),]","");
    }

    public String getIdOfCategoryListString() {
        return idOfCategoryList.toString().replaceAll("[^(0-9-),]","");
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
        if (discountRate != null && discountRate != 100) {
            description = CategoryDiscountEditPage.DISCOUNT_START + discountRate +
                          CategoryDiscountEditPage.DISCOUNT_END;
        }
        entity.setDescription(description);
        String subCategory = "";
        if(this.subCategory > 0) {
            subCategory = RuleCreatePage.SUB_CATEGORIES[this.subCategory];
        }
        entity.setSubCategory(subCategory);

        List<Integer> selectedComplex = Arrays.asList(selectedComplexIds);
        entity.setComplex0(selectedComplex.contains(0) ? 1 : 0);
        entity.setComplex1(selectedComplex.contains(1) ? 1 : 0);
        entity.setComplex2(selectedComplex.contains(2) ? 1 : 0);
        entity.setComplex3(selectedComplex.contains(3) ? 1 : 0);
        entity.setComplex4(selectedComplex.contains(4) ? 1 : 0);
        entity.setComplex5(selectedComplex.contains(5) ? 1 : 0);
        entity.setComplex6(selectedComplex.contains(6) ? 1 : 0);
        entity.setComplex7(selectedComplex.contains(7) ? 1 : 0);
        entity.setComplex8(selectedComplex.contains(8) ? 1 : 0);
        entity.setComplex9(selectedComplex.contains(9) ? 1 : 0);
        entity.setComplex10(selectedComplex.contains(10) ? 1 : 0);
        entity.setComplex11(selectedComplex.contains(11) ? 1 : 0);
        entity.setComplex12(selectedComplex.contains(12) ? 1 : 0);
        entity.setComplex13(selectedComplex.contains(13) ? 1 : 0);
        entity.setComplex14(selectedComplex.contains(14) ? 1 : 0);
        entity.setComplex15(selectedComplex.contains(15) ? 1 : 0);
        entity.setComplex16(selectedComplex.contains(16) ? 1 : 0);
        entity.setComplex17(selectedComplex.contains(17) ? 1 : 0);
        entity.setComplex18(selectedComplex.contains(18) ? 1 : 0);
        entity.setComplex19(selectedComplex.contains(19) ? 1 : 0);
        entity.setComplex20(selectedComplex.contains(20) ? 1 : 0);
        entity.setComplex21(selectedComplex.contains(21) ? 1 : 0);
        entity.setComplex22(selectedComplex.contains(22) ? 1 : 0);
        entity.setComplex23(selectedComplex.contains(23) ? 1 : 0);
        entity.setComplex24(selectedComplex.contains(24) ? 1 : 0);
        entity.setComplex25(selectedComplex.contains(25) ? 1 : 0);
        entity.setComplex26(selectedComplex.contains(26) ? 1 : 0);
        entity.setComplex27(selectedComplex.contains(27) ? 1 : 0);
        entity.setComplex28(selectedComplex.contains(28) ? 1 : 0);
        entity.setComplex29(selectedComplex.contains(29) ? 1 : 0);
        entity.setComplex30(selectedComplex.contains(30) ? 1 : 0);
        entity.setComplex31(selectedComplex.contains(31) ? 1 : 0);
        entity.setComplex32(selectedComplex.contains(32) ? 1 : 0);
        entity.setComplex33(selectedComplex.contains(33) ? 1 : 0);
        entity.setComplex34(selectedComplex.contains(34) ? 1 : 0);
        entity.setComplex35(selectedComplex.contains(35) ? 1 : 0);
        entity.setComplex36(selectedComplex.contains(36) ? 1 : 0);
        entity.setComplex37(selectedComplex.contains(37) ? 1 : 0);
        entity.setComplex38(selectedComplex.contains(38) ? 1 : 0);
        entity.setComplex39(selectedComplex.contains(39) ? 1 : 0);
        entity.setComplex40(selectedComplex.contains(40) ? 1 : 0);
        entity.setComplex41(selectedComplex.contains(41) ? 1 : 0);
        entity.setComplex42(selectedComplex.contains(42) ? 1 : 0);
        entity.setComplex43(selectedComplex.contains(43) ? 1 : 0);
        entity.setComplex44(selectedComplex.contains(44) ? 1 : 0);
        entity.setComplex45(selectedComplex.contains(45) ? 1 : 0);
        entity.setComplex46(selectedComplex.contains(46) ? 1 : 0);
        entity.setComplex47(selectedComplex.contains(47) ? 1 : 0);
        entity.setComplex48(selectedComplex.contains(48) ? 1 : 0);
        entity.setComplex49(selectedComplex.contains(49) ? 1 : 0);
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
        entity.setCodeMSP(DAOReadonlyService.getInstance().findCodeNSPByCode(codeMSP));

        em.persist(entity);
        fill(entity);
        printMessage("Данные обновлены.");
    }

    private void fill(DiscountRule discountRule) throws Exception {
        this.description = discountRule.getDescription();
        if(description.indexOf(CategoryDiscountEditPage.DISCOUNT_START) == 0) {
            String discount = description.substring(
                    description.indexOf(CategoryDiscountEditPage.DISCOUNT_START) +
                            CategoryDiscountEditPage.DISCOUNT_START.length(),
                    description.indexOf(CategoryDiscountEditPage.DISCOUNT_END));
            discountRate = Integer.parseInt(discount);
            description = "";
        } else {
            discountRate = 100;
        }
        subCategory = -1;
        for(int i=0; i<RuleCreatePage.SUB_CATEGORIES.length; i++) {
            if(RuleCreatePage.SUB_CATEGORIES[i].equals(discountRule.getSubCategory())) {
                subCategory = i;
                break;
            }
        }


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
            if(discountRule.getComplex10()>0) comls.add(10);
            if(discountRule.getComplex11()>0) comls.add(11);
            if(discountRule.getComplex12()>0) comls.add(12);
            if(discountRule.getComplex13()>0) comls.add(13);
            if(discountRule.getComplex14()>0) comls.add(14);
            if(discountRule.getComplex15()>0) comls.add(15);
            if(discountRule.getComplex16()>0) comls.add(16);
            if(discountRule.getComplex17()>0) comls.add(17);
            if(discountRule.getComplex18()>0) comls.add(18);
            if(discountRule.getComplex19()>0) comls.add(19);
            if(discountRule.getComplex20()>0) comls.add(20);
            if(discountRule.getComplex21()>0) comls.add(21);
            if(discountRule.getComplex22()>0) comls.add(22);
            if(discountRule.getComplex23()>0) comls.add(23);
            if(discountRule.getComplex24()>0) comls.add(24);
            if(discountRule.getComplex25()>0) comls.add(25);
            if(discountRule.getComplex26()>0) comls.add(26);
            if(discountRule.getComplex27()>0) comls.add(27);
            if(discountRule.getComplex28()>0) comls.add(28);
            if(discountRule.getComplex29()>0) comls.add(29);
            if(discountRule.getComplex30()>0) comls.add(20);
            if(discountRule.getComplex31()>0) comls.add(31);
            if(discountRule.getComplex32()>0) comls.add(32);
            if(discountRule.getComplex33()>0) comls.add(33);
            if(discountRule.getComplex34()>0) comls.add(34);
            if(discountRule.getComplex35()>0) comls.add(35);
            if(discountRule.getComplex36()>0) comls.add(36);
            if(discountRule.getComplex37()>0) comls.add(37);
            if(discountRule.getComplex38()>0) comls.add(38);
            if(discountRule.getComplex39()>0) comls.add(39);
            if(discountRule.getComplex40()>0) comls.add(40);
            if(discountRule.getComplex41()>0) comls.add(41);
            if(discountRule.getComplex42()>0) comls.add(42);
            if(discountRule.getComplex43()>0) comls.add(43);
            if(discountRule.getComplex44()>0) comls.add(44);
            if(discountRule.getComplex45()>0) comls.add(45);
            if(discountRule.getComplex46()>0) comls.add(46);
            if(discountRule.getComplex47()>0) comls.add(47);
            if(discountRule.getComplex48()>0) comls.add(48);
            if(discountRule.getComplex49()>0) comls.add(49);
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

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    @Transactional
    public void reload() throws Exception {
        DiscountRule discountRule = em.merge(entity);

        this.codeMSP = discountRule.getCodeMSP() == null ? null : discountRule.getCodeMSP().getCode();

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