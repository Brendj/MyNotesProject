/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private int complex10;
    private int complex11;
    private int complex12;
    private int complex13;
    private int complex14;
    private int complex15;
    private int complex16;
    private int complex17;
    private int complex18;
    private int complex19;
    private int complex20;
    private int complex21;
    private int complex22;
    private int complex23;
    private int complex24;
    private int complex25;
    private int complex26;
    private int complex27;
    private int complex28;
    private int complex29;
    private int complex30;
    private int complex31;
    private int complex32;
    private int complex33;
    private int complex34;
    private int complex35;
    private int complex36;
    private int complex37;
    private int complex38;
    private int complex39;
    private int complex40;
    private int complex41;
    private int complex42;
    private int complex43;
    private int complex44;
    private int complex45;
    private int complex46;
    private int complex47;
    private int complex48;
    private int complex49;
    private int priority;
    private Boolean operationOr;
    private String categoryDiscounts;
    private Set<CategoryDiscount> categoriesDiscountsInternal = new HashSet<CategoryDiscount>();
    private Set<CategoryOrg> categoryOrgsInternal = new HashSet<CategoryOrg>();
    private String complexesMap;
    private String subCategory;
    private Boolean deletedState;
    private CodeMSP codeMSP;

    public String getComplexesMap() {
        return complexesMap;
    }

    public void setComplexesMap(String complexesMap) {
        this.complexesMap = complexesMap;
    }

    public static final Integer COMPLEX_COUNT = 50;

    public static class ComplexBuilder{

        private HashMap<Integer, Integer> map = new HashMap<Integer, Integer>(COMPLEX_COUNT);

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
            for (int i=0;i<COMPLEX_COUNT;i++){
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
            for (int i=0;i<COMPLEX_COUNT;i++){
                sb.append(i).append("=").append(map.get(i)).append(";");
            }
            return sb.toString();
        }
    }

    public CodeMSP getCodeMSP() {
        return codeMSP;
    }

    public void setCodeMSP(CodeMSP codeMSP) {
        this.codeMSP = codeMSP;
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

    public DiscountRule(long idOfRule, String description,
            int complex0, int complex1, int complex2, int complex3, int complex4, int complex5,
            int complex6, int complex7, int complex8, int complex9,
            int complex10, int complex11, int complex12, int complex13, int complex14, int complex15,
            int complex16, int complex17, int complex18, int complex19,
            int complex20, int complex21, int complex22, int complex23, int complex24, int complex25,
            int complex26, int complex27, int complex28, int complex29,
            int complex30, int complex31, int complex32, int complex33, int complex34, int complex35,
            int complex36, int complex37, int complex38, int complex39,
            int complex40, int complex41, int complex42, int complex43, int complex44, int complex45,
            int complex46, int complex47, int complex48, int complex49,
            int priority,
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
        this.complex10 = complex10;
        this.complex11 = complex11;
        this.complex12 = complex12;
        this.complex13 = complex13;
        this.complex14 = complex14;
        this.complex15 = complex15;
        this.complex16 = complex16;
        this.complex17 = complex17;
        this.complex18 = complex18;
        this.complex19 = complex19;
        this.complex20 = complex20;
        this.complex21 = complex21;
        this.complex22 = complex22;
        this.complex23 = complex23;
        this.complex24 = complex24;
        this.complex25 = complex25;
        this.complex26 = complex26;
        this.complex27 = complex27;
        this.complex28 = complex28;
        this.complex29 = complex29;
        this.complex30 = complex30;
        this.complex31 = complex31;
        this.complex32 = complex32;
        this.complex33 = complex33;
        this.complex34 = complex34;
        this.complex35 = complex35;
        this.complex36 = complex36;
        this.complex37 = complex37;
        this.complex38 = complex38;
        this.complex39 = complex39;
        this.complex40 = complex40;
        this.complex41 = complex41;
        this.complex42 = complex42;
        this.complex43 = complex43;
        this.complex44 = complex44;
        this.complex45 = complex45;
        this.complex46 = complex46;
        this.complex47 = complex47;
        this.complex48 = complex48;
        this.complex49 = complex49;
        this.priority = priority;
        this.operationOr = operationOr;
        this.categoryDiscounts = categoryDiscounts;
        this.deletedState = false;
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

    public int getComplex10() {
        return complex10;
    }

    public void setComplex10(int complex10) {
        this.complex10 = complex10;
    }

    public int getComplex11() {
        return complex11;
    }

    public void setComplex11(int complex11) {
        this.complex11 = complex11;
    }

    public int getComplex12() {
        return complex12;
    }

    public void setComplex12(int complex12) {
        this.complex12 = complex12;
    }

    public int getComplex13() {
        return complex13;
    }

    public void setComplex13(int complex13) {
        this.complex13 = complex13;
    }

    public int getComplex14() {
        return complex14;
    }

    public void setComplex14(int complex14) {
        this.complex14 = complex14;
    }

    public int getComplex15() {
        return complex15;
    }

    public void setComplex15(int complex15) {
        this.complex15 = complex15;
    }

    public int getComplex16() {
        return complex16;
    }

    public void setComplex16(int complex16) {
        this.complex16 = complex16;
    }

    public int getComplex17() {
        return complex17;
    }

    public void setComplex17(int complex17) {
        this.complex17 = complex17;
    }

    public int getComplex18() {
        return complex18;
    }

    public void setComplex18(int complex18) {
        this.complex18 = complex18;
    }

    public int getComplex19() {
        return complex19;
    }

    public void setComplex19(int complex19) {
        this.complex19 = complex19;
    }

    public int getComplex20() {
        return complex20;
    }

    public void setComplex20(int complex20) {
        this.complex20 = complex20;
    }

    public int getComplex21() {
        return complex21;
    }

    public void setComplex21(int complex21) {
        this.complex21 = complex21;
    }

    public int getComplex22() {
        return complex22;
    }

    public void setComplex22(int complex22) {
        this.complex22 = complex22;
    }

    public int getComplex23() {
        return complex23;
    }

    public void setComplex23(int complex23) {
        this.complex23 = complex23;
    }

    public int getComplex24() {
        return complex24;
    }

    public void setComplex24(int complex24) {
        this.complex24 = complex24;
    }

    public int getComplex25() {
        return complex25;
    }

    public void setComplex25(int complex25) {
        this.complex25 = complex25;
    }

    public int getComplex26() {
        return complex26;
    }

    public void setComplex26(int complex26) {
        this.complex26 = complex26;
    }

    public int getComplex27() {
        return complex27;
    }

    public void setComplex27(int complex27) {
        this.complex27 = complex27;
    }

    public int getComplex28() {
        return complex28;
    }

    public void setComplex28(int complex28) {
        this.complex28 = complex28;
    }

    public int getComplex29() {
        return complex29;
    }

    public void setComplex29(int complex29) {
        this.complex29 = complex29;
    }

    public int getComplex30() {
        return complex30;
    }

    public void setComplex30(int complex30) {
        this.complex30 = complex30;
    }

    public int getComplex31() {
        return complex31;
    }

    public void setComplex31(int complex31) {
        this.complex31 = complex31;
    }

    public int getComplex32() {
        return complex32;
    }

    public void setComplex32(int complex32) {
        this.complex32 = complex32;
    }

    public int getComplex33() {
        return complex33;
    }

    public void setComplex33(int complex33) {
        this.complex33 = complex33;
    }

    public int getComplex34() {
        return complex34;
    }

    public void setComplex34(int complex34) {
        this.complex34 = complex34;
    }

    public int getComplex35() {
        return complex35;
    }

    public void setComplex35(int complex35) {
        this.complex35 = complex35;
    }

    public int getComplex36() {
        return complex36;
    }

    public void setComplex36(int complex36) {
        this.complex36 = complex36;
    }

    public int getComplex37() {
        return complex37;
    }

    public void setComplex37(int complex37) {
        this.complex37 = complex37;
    }

    public int getComplex38() {
        return complex38;
    }

    public void setComplex38(int complex38) {
        this.complex38 = complex38;
    }

    public int getComplex39() {
        return complex39;
    }

    public void setComplex39(int complex39) {
        this.complex39 = complex39;
    }

    public int getComplex40() {
        return complex40;
    }

    public void setComplex40(int complex40) {
        this.complex40 = complex40;
    }

    public int getComplex41() {
        return complex41;
    }

    public void setComplex41(int complex41) {
        this.complex41 = complex41;
    }

    public int getComplex42() {
        return complex42;
    }

    public void setComplex42(int complex42) {
        this.complex42 = complex42;
    }

    public int getComplex43() {
        return complex43;
    }

    public void setComplex43(int complex43) {
        this.complex43 = complex43;
    }

    public int getComplex44() {
        return complex44;
    }

    public void setComplex44(int complex44) {
        this.complex44 = complex44;
    }

    public int getComplex45() {
        return complex45;
    }

    public void setComplex45(int complex45) {
        this.complex45 = complex45;
    }

    public int getComplex46() {
        return complex46;
    }

    public void setComplex46(int complex46) {
        this.complex46 = complex46;
    }

    public int getComplex47() {
        return complex47;
    }

    public void setComplex47(int complex47) {
        this.complex47 = complex47;
    }

    public int getComplex48() {
        return complex48;
    }

    public void setComplex48(int complex48) {
        this.complex48 = complex48;
    }

    public int getComplex49() {
        return complex49;
    }

    public void setComplex49(int complex49) {
        this.complex49 = complex49;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public List<Integer> getComplexIdsFromComplexMap(){
        List<String> allMatches = new ArrayList<String>();
        List<Integer> complexIds = new ArrayList<>();
        Matcher matcherPattern = Pattern.compile("\\d+(=1)")
                .matcher(this.complexesMap);
        while (matcherPattern.find()) {
            allMatches.add(matcherPattern.group().replace("=1",""));
        }
        for(String match: allMatches){
            complexIds.add(Integer.parseInt(match));
        }
        return complexIds;
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
                + complex6 + ", complex7=" + complex7 + ", complex8=" + complex8 + ", complex9=" + complex9
                + ", complex10=" + complex10 + ", complex11=" + complex11 + ", complex12=" + complex12 + ", complex13=" + complex13
                + ", complex14=" + complex14 + ", complex15=" + complex15 + ", complex16=" + complex16 + ", complex17=" + complex17
                + ", complex18=" + complex18 + ", complex19=" + complex19
                + ", complex20=" + complex20 + ", complex21=" + complex21 + ", complex22=" + complex22 + ", complex23=" + complex23
                + ", complex24=" + complex24 + ", complex25=" + complex25 + ", complex26=" + complex26 + ", complex27=" + complex27
                + ", complex28=" + complex28 + ", complex29=" + complex29
                + ", complex30=" + complex30 + ", complex31=" + complex31 + ", complex32=" + complex32 + ", complex33=" + complex33
                + ", complex34=" + complex34 + ", complex35=" + complex35 + ", complex36=" + complex36 + ", complex37=" + complex37
                + ", complex38=" + complex38 + ", complex39=" + complex39
                + ", complex40=" + complex40 + ", complex41=" + complex41 + ", complex42=" + complex42 + ", complex43=" + complex43
                + ", complex44=" + complex44 + ", complex45=" + complex45 + ", complex46=" + complex46 + ", complex47=" + complex47
                + ", complex48=" + complex48 + ", complex49=" + complex49 + '}';
    }
}
