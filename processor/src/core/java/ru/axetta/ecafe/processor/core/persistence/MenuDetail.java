/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 27.06.2009
 * Time: 14:02:41
 * To change this template use File | Settings | File Templates.
 */
public class MenuDetail {

    public static final String DEFAULT_GROUP_NAME = "Прочие";

    private Long idOfMenuDetail;
    private Menu menu;
    private String menuDetailName;
    private String menuDetailOutput;
    private Long price;
    private Double protein;
    private Double fat;
    private Double carbohydrates;
    private Double calories;
    private Double vitB1;
    private Double vitB2;
    private Double vitC;
    private Double vitA;
    private Double vitE;
    private Double minCa;
    private Double minP;
    private Double minMg;
    private Double minFe;
    private Double vitPp;
    private int menuOrigin;
    private Long localIdOfMenu;
    private int availableNow;
    private String menuPath;
    private Integer flags;
    private Integer priority;
    private String groupName;
    private Long idOfMenuFromSync;
    private String shortName;

    public Long getIdOfMenuFromSync() {
        return idOfMenuFromSync;
    }

    public void setIdOfMenuFromSync(Long idofmenufromsync) {
        this.idOfMenuFromSync = idofmenufromsync;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }


    public String getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(String menuPath) {
        this.menuPath = menuPath;
    }

    public int getAvailableNow() {
        return availableNow;
    }

    public void setAvailableNow(int availableNow) {
        this.availableNow = availableNow;
    }

    public Long getLocalIdOfMenu() {
        return localIdOfMenu;
    }

    public void setLocalIdOfMenu(Long localIdOfMenu) {
        this.localIdOfMenu = localIdOfMenu;
    }

    public int getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(int menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public MenuDetail() {
    }

    public MenuDetail(Menu menu, String menuPath, String menuDetailName, int menuOrigin, int availableNow,
            Integer flags) {
        this.menuPath = menuPath;
        this.menu = menu;
        this.menuDetailName = menuDetailName;
        this.menuOrigin = menuOrigin;
        this.availableNow = availableNow;
        this.flags = flags;
    }

    public Long getIdOfMenuDetail() {
        return idOfMenuDetail;
    }

    private void setIdOfMenuDetail(Long idOfLocalMenuDetail) {
        // For Hibernate only
        this.idOfMenuDetail = idOfLocalMenuDetail;
    }

    public Menu getMenu() {
        return menu;
    }

    private void setMenu(Menu menu) {
        // For Hibernate only
        this.menu = menu;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    private void setMenuDetailName(String menuDetailName) {
        // For Hibernate only
        this.menuDetailName = menuDetailName;
    }

    public String getMenuDetailOutput() {
        return menuDetailOutput;
    }

    public void setMenuDetailOutput(String menuDetailOutput) {
        this.menuDetailOutput = menuDetailOutput;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        // For Hibernate only
        this.price = price;
    }

    public Double getProtein() {
        return protein;
    }

    public void setProtein(Double protein) {
        this.protein = protein;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getVitB1() {
        return vitB1;
    }

    public void setVitB1(Double vitB1) {
        this.vitB1 = vitB1;
    }

    public Double getVitB2() {
        return vitB2;
    }

    public void setVitB2(Double vitB2) {
        this.vitB2 = vitB2;
    }

    public Double getVitC() {
        return vitC;
    }

    public void setVitC(Double vitC) {
        this.vitC = vitC;
    }

    public Double getVitA() {
        return vitA;
    }

    public void setVitA(Double vitA) {
        this.vitA = vitA;
    }

    public Double getVitE() {
        return vitE;
    }

    public void setVitE(Double vitE) {
        this.vitE = vitE;
    }

    public Double getMinCa() {
        return minCa;
    }

    public void setMinCa(Double minCa) {
        this.minCa = minCa;
    }

    public Double getMinP() {
        return minP;
    }

    public void setMinP(Double minP) {
        this.minP = minP;
    }

    public Double getMinMg() {
        return minMg;
    }

    public void setMinMg(Double minMg) {
        this.minMg = minMg;
    }

    public Double getMinFe() {
        return minFe;
    }

    public void setMinFe(Double minFe) {
        this.minFe = minFe;
    }

    public Double getVitPp() {
        return vitPp;
    }

    public void setVitPp(Double minPp) {
        this.vitPp = minPp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MenuDetail)) {
            return false;
        }
        final MenuDetail that = (MenuDetail) o;
        return idOfMenuDetail.equals(that.getIdOfMenuDetail());
    }

    @Override
    public int hashCode() {
        return idOfMenuDetail.hashCode();
    }

    @Override
    public String toString() {
        return "MenuDetail{" + "idOfMenuDetail=" + idOfMenuDetail + ", menu=" + menu + ", menuDetailName='"
                + menuDetailName + ", menuDetailOutput='" + menuDetailOutput + '\'' + ", price=" + price + ", protein="
                + protein + ", fat=" + fat + ", carbohydrates=" + carbohydrates + ", calories=" + calories + ", vitB1="
                + vitB1 + ", vitB2=" + vitB2 + ", vitC=" + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa="
                + minCa + ", minP=" + minP + ", minMg=" + minMg + ", minFe=" + minFe + ", vitPp=" + vitPp
                + ", menuOrigin=" + menuOrigin + ", flags=" + flags + ", priority=" + priority + ", idOfMenuFromSync=" + idOfMenuFromSync
                + ", shortName=" + shortName + '}';
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
}