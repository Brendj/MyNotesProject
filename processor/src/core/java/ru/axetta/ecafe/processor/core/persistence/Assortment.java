/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: kolpakov
 * Date: 23.02.11
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
public class Assortment {

    private Long idOfAst;
    private Org org;
    private Date beginDate;
    private String shortName;
    private String fullName;
    private String groupName;
    private int menuOrigin;
    private String menuOutput;
    private long price;
    private Double fat;
    private Double carbohydrates;
    private Double calories;
    private Double vitB1;
    private Double vitC;
    private Double vitA;
    private Double vitE;
    private Double minCa;
    private Double minP;
    private Double minMg;
    private Double minFe;

    protected Assortment() {

    }

    public Assortment(Org org, Date beginDate, String shortName, String fullName, String groupName, int menuOrigin,
            String menuOutput, long price, Double fat, Double carbohydrates, Double calories, Double vitB1, Double vitC,
            Double vitA, Double vitE, Double minCa, Double minP, Double minMg, Double minFe) {
        this.org = org;
        this.beginDate = beginDate;
        this.shortName = shortName;
        this.fullName = fullName;
        this.groupName = groupName;
        this.menuOrigin = menuOrigin;
        this.menuOutput = menuOutput;
        this.price = price;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.calories = calories;
        this.vitB1 = vitB1;
        this.vitC = vitC;
        this.vitA = vitA;
        this.vitE = vitE;
        this.minCa = minCa;
        this.minP = minP;
        this.minMg = minMg;
        this.minFe = minFe;
    }

    public Double getMinFe() {
        return minFe;
    }

    public void setMinFe(Double minFe) {
        this.minFe = minFe;
    }

    public Double getMinMg() {
        return minMg;
    }

    public void setMinMg(Double minMg) {
        this.minMg = minMg;
    }

    public Double getMinP() {
        return minP;
    }

    public void setMinP(Double minP) {
        this.minP = minP;
    }

    public Double getMinCa() {
        return minCa;
    }

    public void setMinCa(Double minCa) {
        this.minCa = minCa;
    }

    public Double getVitE() {
        return vitE;
    }

    public void setVitE(Double vitE) {
        this.vitE = vitE;
    }

    public Double getVitA() {
        return vitA;
    }

    public void setVitA(Double vitA) {
        this.vitA = vitA;
    }

    public Double getVitC() {
        return vitC;
    }

    public void setVitC(Double vitC) {
        this.vitC = vitC;
    }

    public Double getVitB1() {
        return vitB1;
    }

    public void setVitB1(Double vitB1) {
        this.vitB1 = vitB1;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public Double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public Double getFat() {
        return fat;
    }

    public void setFat(Double fat) {
        this.fat = fat;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getMenuOutput() {
        return menuOutput;
    }

    public void setMenuOutput(String menuOutput) {
        this.menuOutput = menuOutput;
    }

    public int getMenuOrigin() {
        return menuOrigin;
    }

    public void setMenuOrigin(int menuOrigin) {
        this.menuOrigin = menuOrigin;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Long getIdOfAst() {
        return idOfAst;
    }

    public void setIdOfAst(Long idOfAst) {
        this.idOfAst = idOfAst;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Assortment that = (Assortment) o;

        if (idOfAst != that.idOfAst) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (idOfAst ^ (idOfAst >>> 32));
    }

    @Override
    public String toString() {
        return "Assortment{" + "idOfAst=" + idOfAst + ", org=" + org + ", beginDate=" + beginDate + ", shortName='"
                + shortName + '\'' + ", fullName='" + fullName + '\'' + ", groupName='" + groupName + '\''
                + ", menuOrigin=" + menuOrigin + ", menuOutput='" + menuOutput + '\'' + ", price=" + price + ", fat="
                + fat + ", carbohydrates=" + carbohydrates + ", calories=" + calories + ", vitB1=" + vitB1 + ", vitC="
                + vitC + ", vitA=" + vitA + ", vitE=" + vitE + ", minCa=" + minCa + ", minP=" + minP + ", minMg="
                + minMg + ", minFe=" + minFe + '}';
    }
}
