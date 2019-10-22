/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn;

import ru.axetta.ecafe.processor.core.utils.report.CoverageNutritionDynamicBean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by i.semenov on 17.10.2019.
 */
public class CNReportItem {
    private Long idOfClient;
    private Long idOfClientGroup;
    private String groupName;
    private Long idOfOrg;
    private Integer menuType;
    private Integer menuOrigin;
    private Long rprice;
    private Long discount;
    private Integer qty;
    private String menuDetailName;
    private String groupNameForTemplate;
    private String foodType;
    private String surname;

    public static final List<Integer> listMenuOriginBuffet = Arrays.asList(0, 1, 2, 10, 11, 20);
    public static final List<Long> listSotrudnikov = Arrays.asList(1100000000L, 1100000010L, 1100000020L, 1100000110L);

    public Long getIdOfClient() {
        return idOfClient;
    }

    public CNReportItem(Long idOfClient, Long idOfClientGroup, String groupName, Long idOfOrg, Integer menuType,
            Integer menuOrigin, Long rprice, Long discount, Integer qty, String menuDetailName, String groupNameForTemplate,
            String foodType, String surname) {
        this.idOfClient = idOfClient;
        this.idOfClientGroup = idOfClientGroup;
        this.groupName = groupName;
        this.idOfOrg = idOfOrg;
        this.menuType = menuType;
        this.menuOrigin = menuOrigin;
        this.rprice = rprice;
        this.discount = discount;
        this.qty = qty;
        this.menuDetailName = menuDetailName;
        this.groupNameForTemplate = groupNameForTemplate;
        this.foodType = foodType;
        this.surname = surname;
    }

    public boolean isSotrudnik() {
        return ((listSotrudnikov.contains(idOfClientGroup) || groupName.equals(CoverageNutritionDynamicBean.EMPLOYEES_TITLE)) && !surname.startsWith("#"));
    }

    public boolean isBuffet() {
        if (menuType.equals(0) && listMenuOriginBuffet.contains(menuOrigin)) {
            return true;
        }
        return false;
    }

    public String getComplexName() {
        if (menuType >= 50 && menuType <= 99) {
            return menuDetailName;
        }
        return "";
    }

    public Long getComplexPrice() {
        if (((menuType >= 50 && menuType <= 99) || menuType.equals(0)) && rprice > 0) {
            return rprice;
        }
        if (((menuType >= 50 && menuType <= 99) || menuType.equals(0)) && rprice.equals(0) && discount > 0) {
            return discount;
        }
        return 0L;
        //complexName + priceFormat(price)
    }

    private Long getPriceOrDiscount() {
        return (rprice.equals(0L) ? discount : rprice);
    }

    public String getComplexNameForTemplate() {
        return getComplexName() + priceFormat(getPriceOrDiscount());
    }

    public static String priceFormat(Long price) {
        if (price.equals(0L)) {
            return "";
        }

        Integer rub = new Double(price.doubleValue() / 100.f).intValue();
        Integer cop = new Long(price - rub * 100).intValue();

        String moneyString = String.format(" - %d руб.", rub);

        if (!cop.equals(0)) {
            moneyString += String.format(" %02d коп.", cop);
        }
        return moneyString;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }

    public void setIdOfClientGroup(Long idOfClientGroup) {
        this.idOfClientGroup = idOfClientGroup;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public Integer getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(Integer menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public Long getRprice() {
        return rprice;
    }

    public void setRprice(Long rprice) {
        this.rprice = rprice;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public String getGroupNameForTemplate() {
        return groupNameForTemplate;
    }

    public void setGroupNameForTemplate(String groupNameForTemplate) {
        this.groupNameForTemplate = groupNameForTemplate;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
