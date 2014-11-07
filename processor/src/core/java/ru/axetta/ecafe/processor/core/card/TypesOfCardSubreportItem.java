/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 07.11.14
 * Time: 17:40
 */

public class TypesOfCardSubreportItem {

    private String orgName;
    private String address;

    private Long servActOrgCount;
    private Long servBlockOrgCount;
    private Long skuActOrgCount;
    private Long skuBlockOrgCount;
    private Long othActOrgCount;
    private Long othBlockOrgCount;
    private Long allActOrgCount;
    private Long allBlockOrgCount;

    public TypesOfCardSubreportItem() {
    }

    public TypesOfCardSubreportItem(String orgName, String address, Long servActOrgCount, Long servBlockOrgCount,
            Long skuActOrgCount, Long skuBlockOrgCount, Long othActOrgCount, Long othBlockOrgCount, Long allActOrgCount,
            Long allBlockOrgCount) {
        this.orgName = orgName;
        this.address = address;
        this.servActOrgCount = servActOrgCount;
        this.servBlockOrgCount = servBlockOrgCount;
        this.skuActOrgCount = skuActOrgCount;
        this.skuBlockOrgCount = skuBlockOrgCount;
        this.othActOrgCount = othActOrgCount;
        this.othBlockOrgCount = othBlockOrgCount;
        this.allActOrgCount = allActOrgCount;
        this.allBlockOrgCount = allBlockOrgCount;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getServActOrgCount() {
        return servActOrgCount;
    }

    public void setServActOrgCount(Long servActOrgCount) {
        this.servActOrgCount = servActOrgCount;
    }

    public Long getServBlockOrgCount() {
        return servBlockOrgCount;
    }

    public void setServBlockOrgCount(Long servBlockOrgCount) {
        this.servBlockOrgCount = servBlockOrgCount;
    }

    public Long getSkuActOrgCount() {
        return skuActOrgCount;
    }

    public void setSkuActOrgCount(Long skuActOrgCount) {
        this.skuActOrgCount = skuActOrgCount;
    }

    public Long getSkuBlockOrgCount() {
        return skuBlockOrgCount;
    }

    public void setSkuBlockOrgCount(Long skuBlockOrgCount) {
        this.skuBlockOrgCount = skuBlockOrgCount;
    }

    public Long getOthActOrgCount() {
        return othActOrgCount;
    }

    public void setOthActOrgCount(Long othActOrgCount) {
        this.othActOrgCount = othActOrgCount;
    }

    public Long getOthBlockOrgCount() {
        return othBlockOrgCount;
    }

    public void setOthBlockOrgCount(Long othBlockOrgCount) {
        this.othBlockOrgCount = othBlockOrgCount;
    }

    public Long getAllActOrgCount() {
        return allActOrgCount;
    }

    public void setAllActOrgCount(Long allActOrgCount) {
        this.allActOrgCount = allActOrgCount;
    }

    public Long getAllBlockOrgCount() {
        return allBlockOrgCount;
    }

    public void setAllBlockOrgCount(Long allBlockOrgCount) {
        this.allBlockOrgCount = allBlockOrgCount;
    }
}
