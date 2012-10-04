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
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.applet.AppletContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 9:56
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class RuleListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener {

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        DAOService.getInstance().deleteEntity(confirmDeletePage.getEntity());
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public static class Item {
        DiscountRule entity;
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
        //


        public DiscountRule getEntity() {
            return entity;
        }

        public void setEntity(DiscountRule entity) {
            this.entity = entity;
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
            this.operationor = discountRule.isOperationOr();
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
    private List<Item> items = Collections.emptyList();

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

    @PersistenceContext
    EntityManager em;

    @Transactional
    public void reload() {
        List<Item> items = new ArrayList<Item>();
        List discountRuleList = DAOUtils.listDiscountRules(em);

        for (Object object : discountRuleList) {
            DiscountRule discountRule = (DiscountRule) object;
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
        this.items = items;

    }
}
