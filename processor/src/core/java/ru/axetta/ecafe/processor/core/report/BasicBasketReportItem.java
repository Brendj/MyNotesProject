/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

/**
 * Created by i.semenov on 01.11.2017.
 */
public class BasicBasketReportItem {
    private String nameOfGood;
    private String unitsScale;
    private Long netWeight;
    private String nameOfConfigurationProvider;
    private String menuDetailName;
    private Long rprice;

    public BasicBasketReportItem(String nameOfGood, String unitsScale, Long netWeight,
            String nameOfConfigurationProvider, String menuDetailName, Long rprice) {
        this.nameOfGood = nameOfGood;
        this.unitsScale = unitsScale;
        this.netWeight = netWeight;
        this.nameOfConfigurationProvider = nameOfConfigurationProvider;
        this.menuDetailName = menuDetailName;
        this.rprice = rprice;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }

    public String getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(String unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public String getNameOfConfigurationProvider() {
        return nameOfConfigurationProvider;
    }

    public void setNameOfConfigurationProvider(String nameOfConfigurationProvider) {
        this.nameOfConfigurationProvider = nameOfConfigurationProvider;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public Long getRprice() {
        return rprice;
    }

    public void setRprice(Long rprice) {
        this.rprice = rprice;
    }
}
