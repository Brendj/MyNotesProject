/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discounts.model;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 24.02.16
 * Time: 18:10
 */
public class ClientItem {

    private String name;
    private String category;
    private Integer number;
    private String dsznDiscount;

    public ClientItem() {
    }

    public ClientItem(String name, String category, String dsznDiscount) {
        this.number = 0;
        this.name = name;
        this.category = category + ", ";
        this.dsznDiscount = dsznDiscount;
    }

    public void addCategory(String category) {
        this.category += category + ", ";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category.substring(0, category.length() - 2);
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getDsznDiscount() {
        return dsznDiscount;
    }

    public void setDsznDiscount(String dsznDicsount) {
        this.dsznDiscount = dsznDicsount;
    }
}
