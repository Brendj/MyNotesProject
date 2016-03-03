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

    public ClientItem() {
    }

    public ClientItem(String name, String category) {
        this.name = name;
        this.category = category + ", ";
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
}
