/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

/**
 * Created by anvarov on 15.02.2017.
 */
public class LoadingElementsOfBasicGoodsItem {

    private Long rowNum;
    private String nameOfGood;
    private String unitsScale;
    private String netWeight;
    private String configurationProviderName;
    private String result;

    public LoadingElementsOfBasicGoodsItem() {
    }

    public LoadingElementsOfBasicGoodsItem(Long rowNum, String nameOfGood, String unitsScale, String netWeight, String configurationProviderName, String result) {
        this.rowNum = rowNum;
        this.nameOfGood = nameOfGood;
        this.unitsScale = unitsScale;
        this.netWeight = netWeight;
        this.configurationProviderName = configurationProviderName;
        this.result = result;
    }

    public Long getRowNum() {
        return rowNum;
    }

    public void setRowNum(Long rowNum) {
        this.rowNum = rowNum;
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

    public String getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(String netWeight) {
        this.netWeight = netWeight;
    }

    public String getConfigurationProviderName() {
        return configurationProviderName;
    }

    public void setConfigurationProviderName(String configurationProviderName) {
        this.configurationProviderName = configurationProviderName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
