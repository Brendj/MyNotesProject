/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client.rule;

import ru.axetta.ecafe.processor.core.persistence.DiscountRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 22.11.11
 * Time: 9:56
 * To change this template use File | Settings | File Templates.
 */
public class RuleListPage extends BasicWorkspacePage {
    public static class Item {
        private long idOfRule;
        private String categoryName;
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
        private int complexes;

        //
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
            this.categoryName = discountRule.getCategoryDiscount().getCategoryName();
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
            this.complexes = discountRule.getComplexes();
            this.priority = discountRule.getPriority();
        }

        public long getIdOfRule() {
            return idOfRule;
        }

        public String getCategoryName() {
            return categoryName;
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

        public int getComplexes() {
            return complexes;
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
        return "client/rule/list";
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new ArrayList<Item>();
        Criteria ruleCriteria = session.createCriteria(DiscountRule.class);
        ruleCriteria.addOrder(Order.asc("idOfRule"));
        List discountRuleList = ruleCriteria.list();
        for (Object object : discountRuleList) {
            DiscountRule discountRule = (DiscountRule) object;
            items.add(new Item(discountRule));
        }
        this.items = items;
    }
}
