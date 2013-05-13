/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 05.12.11
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class DiscountRule {
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
    private int priority;
    private Boolean operationOr;
    private String categoryDiscounts;
    private Set<CategoryDiscount> categoriesDiscountsInternal = new HashSet<CategoryDiscount>();
    private Set<CategoryOrg> categoryOrgsInternal = new HashSet<CategoryOrg>();
    private String complexesMap;

    public String getComplexesMap() {
        return complexesMap;
    }

    public void setComplexesMap(String complexesMap) {
        this.complexesMap = complexesMap;
    }

    public static class ComplexBuilder{

        private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(50);

        public ComplexBuilder(String value) {
            String[] values = value.split(";");
            for (String val: values){
                String[] keyVal = val.split("=");
                int k = Integer.parseInt(keyVal[0]);
                int v = Integer.parseInt(keyVal[1]);
                map.put(k,v);
            }
        }

        public ComplexBuilder(List<Integer> checkedList) {
            for (int i=0;i<50;i++){
                map.put(i,0);
            }
            for (Integer i: checkedList){
                map.put(i,1);
            }
        }

        public HashMap<Integer, Integer> getMap() {
            return map;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i=0;i<50;i++){
                sb.append(i).append("=").append(map.get(i)).append(";");
            }
            return sb.toString();
        }
    }


    public Set<CategoryOrg> getCategoryOrgs() {
        return getCategoryOrgsInternal();
    }

    private void setCategoryOrgs(Set<CategoryOrg> categoryOrgs) {
        this.categoryOrgsInternal = categoryOrgs;
    }

    private Set<CategoryOrg> getCategoryOrgsInternal() {
        return categoryOrgsInternal;
    }

    private void setCategoryOrgsInternal(Set<CategoryOrg> categoryOrgsInternal) {
        this.categoryOrgsInternal = categoryOrgsInternal;
    }

    public Set<CategoryDiscount> getCategoriesDiscounts(){
        return getCategoriesDiscountsInternal();
    }

    public void setCategoriesDiscounts(Set<CategoryDiscount> categoriesDiscountsInternal) {
        this.categoriesDiscountsInternal = categoriesDiscountsInternal;
    }

    private Set<CategoryDiscount> getCategoriesDiscountsInternal() {
        return categoriesDiscountsInternal;
    }

    private void setCategoriesDiscountsInternal(Set<CategoryDiscount> categoriesDiscountsInternal) {
        this.categoriesDiscountsInternal = categoriesDiscountsInternal;
    }

    public String getCategoryDiscounts() {
        return categoryDiscounts;
    }

    public void setCategoryDiscounts(String categoryDiscounts) {
        this.categoryDiscounts = categoryDiscounts;
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

    public DiscountRule() {
    }

    public DiscountRule(long idOfRule, String description, int complex0, int complex1, int complex2, int complex3,
            int complex4, int complex5, int complex6, int complex7, int complex8, int complex9, int priority,
            boolean operationOr, String categoryDiscounts) {
        this.idOfRule = idOfRule;
        this.description = description;
        this.complex0 = complex0;
        this.complex1 = complex1;
        this.complex2 = complex2;
        this.complex3 = complex3;
        this.complex4 = complex4;
        this.complex5 = complex5;
        this.complex6 = complex6;
        this.complex7 = complex7;
        this.complex8 = complex8;
        this.complex9 = complex9;
        this.priority = priority;
        this.operationOr = operationOr;
        this.categoryDiscounts = categoryDiscounts;
    }

    public long getIdOfRule() {
        return idOfRule;
    }

    public void setIdOfRule(long idOfRule) {
        this.idOfRule = idOfRule;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getComplex0() {
        return complex0;
    }

    public void setComplex0(int complex0) {
        this.complex0 = complex0;
    }

    public int getComplex1() {
        return complex1;
    }

    public void setComplex1(int complex1) {
        this.complex1 = complex1;
    }

    public int getComplex2() {
        return complex2;
    }

    public void setComplex2(int complex2) {
        this.complex2 = complex2;
    }

    public int getComplex3() {
        return complex3;
    }

    public void setComplex3(int complex3) {
        this.complex3 = complex3;
    }

    public int getComplex4() {
        return complex4;
    }

    public void setComplex4(int complex4) {
        this.complex4 = complex4;
    }

    public int getComplex5() {
        return complex5;
    }

    public void setComplex5(int complex5) {
        this.complex5 = complex5;
    }

    public int getComplex6() {
        return complex6;
    }

    public void setComplex6(int complex6) {
        this.complex6 = complex6;
    }

    public int getComplex7() {
        return complex7;
    }

    public void setComplex7(int complex7) {
        this.complex7 = complex7;
    }

    public int getComplex8() {
        return complex8;
    }

    public void setComplex8(int complex8) {
        this.complex8 = complex8;
    }

    public int getComplex9() {
        return complex9;
    }

    public void setComplex9(int complex9) {
        this.complex9 = complex9;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DiscountRule that = (DiscountRule) o;

        if (idOfRule != that.idOfRule) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfRule ^ (idOfRule >>> 32));
    }

    @Override
    public String toString() {
        return "DiscountRule{" + "idOfRule=" + idOfRule  + ", description='"
                + description + '\'' + ", complex0=" + complex0 + ", complex1=" + complex1 + ", complex2=" + complex2
                + ", complex3=" + complex3 + ", complex4=" + complex4 + ", complex5=" + complex5 + ", complex6="
                + complex6 + ", complex7=" + complex7 + ", complex8=" + complex8 + ", complex9=" + complex9 + '}';
    }
}
